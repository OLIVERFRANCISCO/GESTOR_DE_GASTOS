package com.oliver.gestor_de_gastos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.controller.CategoriaController
import com.oliver.gestor_de_gastos.controller.FinanzasController
import com.oliver.gestor_de_gastos.model.Categoria

@Composable
fun CategoriasView(

) {
    val context = LocalContext.current
    val controller = remember { FinanzasController(context) }
    val categoriaController = remember { CategoriaController(context) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var showAddCategoriaDialog by remember { mutableStateOf(false) }
    var nuevaCategoriaNombre by remember { mutableStateOf("") }
    var categoriaEnEdicion by remember { mutableStateOf<Categoria?>(null) }
    var showEditCategoriaDialog by remember { mutableStateOf(false) }
    var editCategoriaNombre by remember { mutableStateOf("") }
    var categoriaMenuExpandedId by remember { mutableStateOf<Int?>(null) }
    var showCategoriaExistenteDialog by remember { mutableStateOf(false) }
    var categoriaParaVerRegistros by remember { mutableStateOf<Categoria?>(null) }
    var registrosCategoria by remember { mutableStateOf(listOf<Registro>()) }


    // Cargar categorías al iniciar la vista y tras cambios
    LaunchedEffect(Unit) {
        categorias = categoriaController.obtenerCategorias()
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Categorías de gastos:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showAddCategoriaDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Agregar categoría")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        categorias.forEach { cat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        categoriaParaVerRegistros = cat
                        registrosCategoria = controller.obtenerRegistrosPorCategoria(cat.id).map {
                            Registro(
                                fecha = it.fecha,
                                monto = it.monto.toDouble(),
                                descripcion = it.descripcion
                            )
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(cat.nombre, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        categoriaEnEdicion = cat
                        editCategoriaNombre = cat.nombre
                        showEditCategoriaDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        categoriaController.eliminarCategoria(cat.id)
                        categorias = categoriaController.obtenerCategorias()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }
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
                            categorias = categoriaController.obtenerCategorias()
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
                        categorias = categoriaController.obtenerCategorias()
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
