package com.oliver.gestor_de_gastos.controller

import android.content.Context
import com.oliver.gestor_de_gastos.DatabaseHelper

class CategoriaController(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun registrarCategoria(nombre: String) {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO Categorias (nombre) VALUES (?)", arrayOf(nombre))
        db.close()
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

