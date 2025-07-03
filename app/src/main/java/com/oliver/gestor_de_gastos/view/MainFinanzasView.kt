package com.oliver.gestor_de_gastos.view



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.DatabaseHelper
import com.oliver.gestor_de_gastos.controller.CategoriaController
import com.oliver.gestor_de_gastos.controller.FinanzasController
import com.oliver.gestor_de_gastos.model.Categoria


@Composable
fun MainFinanzasView(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val controller = remember { FinanzasController(context) }
    val categoriaController = remember { CategoriaController(context) }
    var saldo by remember { mutableStateOf(0) }
    var gastoTotal by remember { mutableStateOf(0) }
    var categorias by remember { mutableStateOf(controller.obtenerCategorias()) }
    var showDialog by remember { mutableStateOf(false) }
    var montoGasto by remember { mutableStateOf(0) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var descripcionGasto by remember { mutableStateOf("") }
    var showAddCategoriaDialog by remember { mutableStateOf(false) }
    var nuevaCategoriaNombre by remember { mutableStateOf("") }
    var categoriaEnEdicion by remember { mutableStateOf<Categoria?>(null) }
    var showEditCategoriaDialog by remember { mutableStateOf(false) }
    var editCategoriaNombre by remember { mutableStateOf("") }
    var categoriaMenuExpandedId by remember { mutableStateOf<Int?>(null) }
    var showIngresoDialog by remember { mutableStateOf(false) }
    var montoIngreso by remember { mutableStateOf(0) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showCategoriaExistenteDialog by remember { mutableStateOf(false) }
    var categoriaParaVerRegistros by remember { mutableStateOf<Categoria?>(null) }
    var registrosCategoria by remember { mutableStateOf(listOf<Registro>()) }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        saldo = controller.obtenerSaldoActual()
        gastoTotal = controller.obtenerGastoTotal()
        categorias = controller.obtenerCategorias()
    }

    if (categoriaParaVerRegistros != null) {
        ViewRegistroCategoria(
            categoriaNombre = categoriaParaVerRegistros!!.nombre,
            registros = registrosCategoria,
            onBack = {
                categoriaParaVerRegistros = null
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Saldo actual: $saldo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            // Botón de configuración eliminado para cumplir con la navegación inferior
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Gasto total: $gastoTotal", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Categorías de gastos:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { showAddCategoriaDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {
                Text("Agregar categoría")
            }
        }
        LazyColumn(modifier = Modifier.height(420.dp)) {
            items(categorias) { cat ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.background, shape = RectangleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, shape = RectangleShape)
                        .padding(8.dp)
                        .clickable {
                            categoriaParaVerRegistros = cat
                            registrosCategoria = controller.obtenerRegistrosPorCategoria(cat.id).map {
                                Registro(
                                    fecha = it.fecha,
                                    monto = it.monto.toDouble(),
                                    descripcion = it.descripcion
                                )
                            }
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(cat.nombre, modifier = Modifier.weight(1f))
                        Box {
                            // Menú de opciones de categoría sin iconos
                            Text(
                                text = "⋮",
                                modifier = Modifier
                                    .clickable { categoriaMenuExpandedId = cat.id }
                                    .padding(horizontal = 8.dp)
                            )
                            DropdownMenu(
                                expanded = categoriaMenuExpandedId == cat.id,
                                onDismissRequest = { categoriaMenuExpandedId = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {
                                        categoriaEnEdicion = cat
                                        editCategoriaNombre = cat.nombre
                                        showEditCategoriaDialog = true
                                        categoriaMenuExpandedId = null
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        categoriaController.eliminarCategoria(cat.id)
                                        categorias = controller.obtenerCategorias()
                                        categoriaMenuExpandedId = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showIngresoDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Registrar ingreso")
            }
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
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
                        label = { Text("Descripción") }
                    )
                    OutlinedTextField(
                        value = montoGasto.toString(),
                        onValueChange = { value -> montoGasto = value.toIntOrNull() ?: 0 },
                        label = { Text("Monto") }
                    )
                    DropdownMenuCategorias(categorias, categoriaSeleccionada) { categoriaSeleccionada = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (categoriaSeleccionada != null && montoGasto > 0) {
                        controller.registrarGasto(categoriaSeleccionada!!.id, montoGasto, descripcionGasto)
                        saldo = controller.obtenerSaldoActual()
                        gastoTotal = controller.obtenerGastoTotal()
                        showDialog = false
                        descripcionGasto = ""
                        montoGasto = 0
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
    if (showAddCategoriaDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoriaDialog = false },
            title = { Text("Agregar nueva categoría") },
            text = {
                OutlinedTextField(
                    value = nuevaCategoriaNombre,
                    onValueChange = { nuevaCategoriaNombre = it },
                    label = { Text("Nombre de la categoría") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (nuevaCategoriaNombre.isNotBlank()) {
                        val registrada = categoriaController.registrarCategoria(nuevaCategoriaNombre)
                        if (registrada) {
                            categorias = controller.obtenerCategorias()
                            nuevaCategoriaNombre = ""
                            showAddCategoriaDialog = false
                        } else {
                            nuevaCategoriaNombre = ""
                            showCategoriaExistenteDialog = true
                        }
                    }
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                Button(onClick = { showAddCategoriaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showEditCategoriaDialog && categoriaEnEdicion != null) {
        AlertDialog(
            onDismissRequest = { showEditCategoriaDialog = false },
            title = { Text("Editar categoría") },
            text = {
                OutlinedTextField(
                    value = editCategoriaNombre,
                    onValueChange = { editCategoriaNombre = it },
                    label = { Text("Nuevo nombre") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (editCategoriaNombre.isNotBlank()) {
                        categoriaController.editarCategoria(categoriaEnEdicion!!.id, editCategoriaNombre)
                        categorias = controller.obtenerCategorias()
                        showEditCategoriaDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showEditCategoriaDialog = false }) {
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
                    value = montoIngreso.toString(),
                    onValueChange = { value -> montoIngreso = value.toIntOrNull() ?: 0 },
                    label = { Text("Monto a ingresar") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (montoIngreso > 0) {
                        controller.registrarIngreso(montoIngreso)
                        saldo = controller.obtenerSaldoActual()
                        montoIngreso = 0
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
    if (showConfigDialog) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = { Text("Configuración") },
            text = {
                Column {
                    Text("Tema de la app:")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { onThemeChange(false) },
                            enabled = isDarkTheme
                        ) { Text("Claro") }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onThemeChange(true) },
                            enabled = !isDarkTheme
                        ) { Text("Oscuro") }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showConfigDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
    if (showCategoriaExistenteDialog) {
        AlertDialog(
            onDismissRequest = { showCategoriaExistenteDialog = false },
            title = { Text("Error") },
            text = { Text("El nombre de la categoría ya existe.") },
            confirmButton = {
                Button(onClick = { showCategoriaExistenteDialog = false }) {
                    Text("Aceptar")
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
