package com.oliver.gestor_de_gastos.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.model.Gasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRegistroCategoria(
    categoriaNombre: String,
    registros: List<Gasto>,
    onBack: () -> Unit,
    onEdit: (Registro) -> Unit = {},
    onDelete: (Registro) -> Unit = {}
) {
    var menuExpandedIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var registroAEliminar by remember { mutableStateOf<Registro?>(null) }
    var registroAEditar by remember { mutableStateOf<Registro?>(null) }
    var descripcionEdit by remember { mutableStateOf("") }
    var montoEdit by remember { mutableStateOf(0.0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Registros de $categoriaNombre") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Encabezado de la tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
        ) {
            Text("Fecha", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Text("Monto", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Text("Descripción", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(registros) { idx, registro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(registro.fecha, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(String.format("%.2f", registro.monto), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(registro.descripcion, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                        Box {
                            IconButton(onClick = { menuExpandedIndex = idx }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Opciones")
                            }
                            DropdownMenu(
                                expanded = menuExpandedIndex == idx,
                                onDismissRequest = { menuExpandedIndex = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {

                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    // Diálogo de edición
    if (registroAEditar != null) {
        AlertDialog(
            onDismissRequest = { registroAEditar = null },
            title = { Text("Editar registro") },
            text = {
                Column {
                    OutlinedTextField(
                        value = descripcionEdit,
                        onValueChange = { descripcionEdit = it },
                        label = { Text("Descripción") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = montoEdit.toString(),
                        onValueChange = { value -> montoEdit = value.replace(',', '.').toDoubleOrNull() ?: 0.0 },
                        label = { Text("Monto") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    registroAEditar?.let {
                        onEdit(it.copy(descripcion = descripcionEdit, monto = montoEdit))
                    }
                    registroAEditar = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                Button(onClick = { registroAEditar = null }) { Text("Cancelar") }
            }
        )
    }
    // Diálogo de confirmación de borrado
    if (showDeleteDialog && registroAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar registro") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro?") },
            confirmButton = {
                Button(onClick = {
                    registroAEliminar?.let { onDelete(it) }
                    showDeleteDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// Modelo de ejemplo para Registro
// Reemplaza esto por tu modelo real
data class Registro(val fecha: String, val monto: Double, val descripcion: String)
