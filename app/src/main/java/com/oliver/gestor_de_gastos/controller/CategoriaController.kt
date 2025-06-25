package com.oliver.gestor_de_gastos.controller

import android.content.Context
import com.oliver.gestor_de_gastos.DatabaseHelper

class CategoriaController(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun registrarCategoria(nombre: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Categorias WHERE nombre = ?", arrayOf(nombre))
        var existe = false
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0
        }
        cursor.close()
        if (existe) {
            db.close()
            return false // Ya existe
        }
        db.close()
        val dbWrite = dbHelper.writableDatabase
        dbWrite.execSQL("INSERT INTO Categorias (nombre) VALUES (?)", arrayOf(nombre))
        dbWrite.close()
        return true
    }

    fun editarCategoria(id: Int, nuevoNombre: String) {
        val db = dbHelper.writableDatabase
        db.execSQL("UPDATE Categorias SET nombre = ? WHERE id = ?", arrayOf(nuevoNombre, id))
        db.close()
    }

    fun eliminarCategoria(id: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM Categorias WHERE id = ?", arrayOf(id))
        db.close()
    }
}
