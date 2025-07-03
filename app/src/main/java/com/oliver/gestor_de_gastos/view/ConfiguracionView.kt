package com.oliver.gestor_de_gastos.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.widget.Toast
import com.oliver.gestor_de_gastos.DatabaseHelper
import androidx.compose.ui.unit.dp

@Composable
fun ConfiguracionView(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }
    var showResetSuccess by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { showResetDialog = true }) {
            Text("Resetear base de datos")
        }
    }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("¿Resetear base de datos?") },
            text = { Text("Esta acción eliminará todos los datos. ¿Desea continuar?") },
            confirmButton = {
                Button(onClick = {
                    val dbHelper = DatabaseHelper(context)
                    dbHelper.writableDatabase.apply {
                        dbHelper.onUpgrade(this, 1, 1)
                        close()
                    }
                    showResetDialog = false
                    showResetSuccess = true
                }) { Text("Sí, resetear") }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) { Text("Cancelar") }
            }
        )
    }
    if (showResetSuccess) {
        AlertDialog(
            onDismissRequest = { showResetSuccess = false },
            title = { Text("Base de datos reseteada") },
            text = { Text("La base de datos se ha reseteado correctamente.") },
            confirmButton = {
                Button(onClick = { showResetSuccess = false }) { Text("Aceptar") }
            }
        )
    }
}
