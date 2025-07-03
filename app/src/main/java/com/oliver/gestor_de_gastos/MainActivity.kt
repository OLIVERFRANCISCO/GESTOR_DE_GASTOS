package com.oliver.gestor_de_gastos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oliver.gestor_de_gastos.ui.theme.Gestor_de_gastosTheme
import com.oliver.gestor_de_gastos.view.CategoriasView
import com.oliver.gestor_de_gastos.view.ConfiguracionView
import com.oliver.gestor_de_gastos.view.MainFinanzasView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inicializarBaseDeDatos()
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var selectedIndex by remember { mutableStateOf(0) }
            val context = LocalContext.current

            // Detectar intento de rotación
            DisposableEffect(Unit) {
                val callback = object : android.view.OrientationEventListener(context) {
                    override fun onOrientationChanged(orientation: Int) {
                        if (orientation in 80..100 || orientation in 260..280) {
                            Toast.makeText(context, "No es posible girar la pantalla", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                callback.enable()
                onDispose { callback.disable() }
            }

            Gestor_de_gastosTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = {},
                                label = { Text("Inicio") },
                                selected = selectedIndex == 0,
                                onClick = { selectedIndex = 0 }
                            )
                            NavigationBarItem(
                                icon = { },
                                label = { Text("Categorías") },
                                selected = selectedIndex == 1,
                                onClick = { selectedIndex = 1 }
                            )
                            NavigationBarItem(
                                icon = {},
                                label = { Text("Configuración") },
                                selected = selectedIndex == 2,
                                onClick = { selectedIndex = 2 }
                            )
                        }
                    }
                ) { padding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .pointerInput(selectedIndex) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount > 50 && selectedIndex > 0) selectedIndex--
                                    if (dragAmount < -50 && selectedIndex < 2) selectedIndex++
                                }
                            }
                    ) {
                        when (selectedIndex) {
                            0 -> MainFinanzasView(isDarkTheme = isDarkTheme, onThemeChange = { isDarkTheme = it })
                            1 -> CategoriasView()
                            2 -> ConfiguracionView(isDarkTheme = isDarkTheme, onThemeChange = { isDarkTheme = it })
                        }
                    }
                }
            }
        }
    }

    private fun inicializarBaseDeDatos() {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        db.rawQuery("SELECT id FROM Categorias LIMIT 1", null).use { cursorCat ->
            db.rawQuery("SELECT id FROM Transacciones LIMIT 1", null).use { cursorTrans ->
                // Aquí puedes manejar los IDs si es necesario
            }
        }
    }
}