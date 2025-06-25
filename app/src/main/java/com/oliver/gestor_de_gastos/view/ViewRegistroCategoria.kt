package com.oliver.gestor_de_gastos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRegistroCategoria(
    categoriaNombre: String,
    registros: List<Registro>,
    onBack: () -> Unit
) {
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
        // Tabla de registros
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp)
                ) {
                    Text("Fecha", modifier = Modifier.weight(1f))
                    Text("Monto", modifier = Modifier.weight(1f))
                    Text("Descripción", modifier = Modifier.weight(2f))
                }
            }
            items(registros) { registro ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(registro.fecha, modifier = Modifier.weight(1f))
                    Text(registro.monto.toString(), modifier = Modifier.weight(1f))
                    Text(registro.descripcion, modifier = Modifier.weight(2f))
                }
            }
        }
    }
}

// Modelo de ejemplo para Registro
// Reemplaza esto por tu modelo real
data class Registro(val fecha: String, val monto: Double, val descripcion: String)

