package com.oliver.gestor_de_gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.oliver.gestor_de_gastos.ui.theme.Gestor_de_gastosTheme
import com.oliver.gestor_de_gastos.view.MainFinanzasView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar y poblar la base de datos
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        // Obtener IDs para relaciones
        val cursorCat = db.rawQuery("SELECT id FROM Categorias LIMIT 1", null)
        val cursorTrans = db.rawQuery("SELECT id FROM Transacciones LIMIT 1", null)
        var idCat = 1
        var idTrans = 1
        if (cursorCat.moveToFirst()) idCat = cursorCat.getInt(0)
        if (cursorTrans.moveToFirst()) idTrans = cursorTrans.getInt(0)
        cursorCat.close()
        cursorTrans.close()
        enableEdgeToEdge()
        // Estado para el tema
        var isDarkTheme = mutableStateOf(false)
        setContent {
            Gestor_de_gastosTheme(darkTheme = isDarkTheme.value) {
                MainFinanzasView(
                    isDarkTheme = isDarkTheme.value,
                    onThemeChange = { isDarkTheme.value = it }
                )
            }
        }
    }
}
