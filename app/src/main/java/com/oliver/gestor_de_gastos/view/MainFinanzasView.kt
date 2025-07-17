package com.oliver.gestor_de_gastos.view



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.controller.CategoriaController
import com.oliver.gestor_de_gastos.controller.FinanzasController
import com.oliver.gestor_de_gastos.model.Categoria
import java.util.Calendar


@Composable
fun MainFinanzasView(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val controller = remember { FinanzasController(context) }
    val categoriaController = remember { CategoriaController(context) }
    var saldo by remember { mutableStateOf(0.0) }
    var gastoTotal by remember { mutableStateOf(0.0) }
    var categorias by remember { mutableStateOf(controller.obtenerCategorias()) }
    var showDialog by remember { mutableStateOf(false) }
    var montoGasto by remember { mutableStateOf(0.0) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var descripcionGasto by remember { mutableStateOf("") }
    var showIngresoDialog by remember { mutableStateOf(false) }
    var montoIngreso by remember { mutableStateOf(0.0) }

    val calendar = Calendar.getInstance()
    val meses = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    val anioActual = calendar.get(Calendar.YEAR)
    val mesActual = calendar.get(Calendar.MONTH)
    var mesSeleccionado by remember { mutableStateOf(mesActual) }
    var anioSeleccionado by remember { mutableStateOf(anioActual) }
    var datosGrafico by remember { mutableStateOf(emptyMap<Categoria, Double>()) }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        saldo = controller.obtenerSaldoActual()
        gastoTotal = controller.obtenerGastoTotal()
        categorias = controller.obtenerCategorias()
        datosGrafico = categoriaController.obtenerGastosPorCategoriaEnMes(mesSeleccionado + 1, anioSeleccionado)
    }

    // Actualizar datos del gráfico al cambiar mes/año
    LaunchedEffect(mesSeleccionado, anioSeleccionado) {
        datosGrafico = categoriaController.obtenerGastosPorCategoriaEnMes(mesSeleccionado + 1, anioSeleccionado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Saldo actual: $saldo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Gasto total: $gastoTotal", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Menú desplegable de mes y año en la parte superior del gráfico
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            // Dropdown de mes
            var expandedMes by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expandedMes = true }) {
                    Text(meses[mesSeleccionado])
                }
                DropdownMenu(expanded = expandedMes, onDismissRequest = { expandedMes = false }) {
                    meses.forEachIndexed { idx, mes ->
                        DropdownMenuItem(text = { Text(mes) }, onClick = {
                            mesSeleccionado = idx
                            expandedMes = false
                        })
                    }
                }
            }
            // Dropdown de año (últimos 5 años)
            var expandedAnio by remember { mutableStateOf(false) }
            val anios = (anioActual downTo anioActual - 4).toList()
            Box {
                OutlinedButton(onClick = { expandedAnio = true }) {
                    Text(anioSeleccionado.toString())
                }
                DropdownMenu(expanded = expandedAnio, onDismissRequest = { expandedAnio = false }) {
                    anios.forEach { anio ->
                        DropdownMenuItem(text = { Text(anio.toString()) }, onClick = {
                            anioSeleccionado = anio
                            expandedAnio = false
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            if (datosGrafico.isNotEmpty()) {
                ChartPay(
                    porcentajes = datosGrafico.values.map { it.toFloat() }.toFloatArray(),
                    labels = datosGrafico.keys.map { it.nombre }
                )
            } else {
                Text("No hay datos para este mes/año", modifier = Modifier.padding(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showIngresoDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Registrar ingreso")
            }
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Registrar gasto")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Registrar gasto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = descripcionGasto,
                        onValueChange = { descripcionGasto = it },
                        label = { Text("Descripción") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = if (montoGasto == 0.0) "" else montoGasto.toString(),
                        onValueChange = { value ->
                            montoGasto = value.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Monto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownMenuCategorias(categorias, categoriaSeleccionada) { categoriaSeleccionada = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (categoriaSeleccionada != null && montoGasto > 0.0) {
                        controller.registrarGasto(categoriaSeleccionada!!.id, montoGasto, descripcionGasto)
                        saldo = controller.obtenerSaldoActual()
                        gastoTotal = controller.obtenerGastoTotal()
                        showDialog = false
                        descripcionGasto = ""
                        montoGasto = 0.0
                    }
                }) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showIngresoDialog) {
        AlertDialog(
            onDismissRequest = { showIngresoDialog = false },
            title = { Text("Registrar ingreso") },
            text = {
                OutlinedTextField(
                    value = if (montoIngreso == 0.0) "" else montoIngreso.toString(),
                    onValueChange = { value ->
                        montoIngreso = value.toDoubleOrNull() ?: 0.0
                    },
                    label = { Text("Monto a ingresar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (montoIngreso > 0.0) {
                        controller.registrarIngreso(montoIngreso)
                        saldo = controller.obtenerSaldoActual()
                        montoIngreso = 0.0
                        showIngresoDialog = false
                    }
                }) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                Button(onClick = { showIngresoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun DropdownMenuCategorias(
    categorias: List<Categoria>,
    seleccionada: Categoria?,
    onSelected: (Categoria) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(seleccionada?.nombre ?: "Selecciona categoría")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categorias.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.nombre) },
                    onClick = {
                        onSelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainFinanzasViewPreview() {
    MainFinanzasView(isDarkTheme = false, onThemeChange = {})
}
