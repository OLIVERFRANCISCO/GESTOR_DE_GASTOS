package com.oliver.gestor_de_gastos.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.controller.CategoriaController
import com.oliver.gestor_de_gastos.model.Categoria

@Composable
fun CategoriasView() {
    val context = LocalContext.current
    val categoriaController = remember { CategoriaController(context) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var showAddCategoriaDialog by remember { mutableStateOf(false) }
    var nuevaCategoriaNombre by remember { mutableStateOf("") }
    var categoriaEnEdicion by remember { mutableStateOf<Categoria?>(null) }
    var showEditCategoriaDialog by remember { mutableStateOf(false) }
    var editCategoriaNombre by remember { mutableStateOf("") }
    var categoriaMenuExpandedId by remember { mutableStateOf<Int?>(null) }
    var showCategoriaExistenteDialog by remember { mutableStateOf(false) }

    // Cargar categorías al iniciar la vista y tras cambios
    LaunchedEffect(showAddCategoriaDialog, showEditCategoriaDialog, showCategoriaExistenteDialog) {
        categorias = categoriaController.obtenerCategorias()
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Categorías de gastos:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { showAddCategoriaDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {
                Text("Agregar categoría")
            }
        }
//        LazyColumn(modifier = Modifier.height(420.dp)) {
//            items(categorias, key = { it.id }) { cat ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                        .padding(8.dp)
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(cat.nombre, modifier = Modifier.weight(1f))
//                        Box {
//                            Text(
//                                text = "⋮",
//                                modifier = Modifier
//                                    .clickable { categoriaMenuExpandedId = cat.id }
//                                    .padding(horizontal = 8.dp)
//                            )
//                            DropdownMenu(
//                                expanded = categoriaMenuExpandedId == cat.id,
//                                onDismissRequest = { categoriaMenuExpandedId = null }
//                            ) {
//                                DropdownMenuItem(
//                                    text = { Text("Editar") },
//                                    onClick = {
//                                        categoriaEnEdicion = cat
//                                        editCategoriaNombre = cat.nombre
//                                        showEditCategoriaDialog = true
//                                        categoriaMenuExpandedId = null
//                                    }
//                                )
//                                DropdownMenuItem(
//                                    text = { Text("Eliminar") },
//                                    onClick = {
//                                        categoriaController.eliminarCategoria(cat.id)
//                                        // La lista se recarga automáticamente por LaunchedEffect
//                                        categoriaMenuExpandedId = null
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
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
