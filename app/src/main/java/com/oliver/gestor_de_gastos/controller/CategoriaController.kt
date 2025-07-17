package com.oliver.gestor_de_gastos.controller

import android.content.Context
import com.oliver.gestor_de_gastos.DatabaseHelper
import com.oliver.gestor_de_gastos.model.Categoria

class CategoriaController(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)


    fun obtenerGastosPorCategoriaEnMes(mes: Int, anio: Int): Map<Categoria, Double> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT c.id, c.nombre, SUM(g.monto) as total
            FROM Gastos g
            JOIN Categorias c ON g.id_categoria = c.id
            WHERE strftime('%m', g.Fecha_registro) = ? AND strftime('%Y', g.Fecha_registro) = ?
            GROUP BY c.id, c.nombre
        """
        val mesStr = mes.toString().padStart(2, '0')
        val anioStr = anio.toString()
        val cursor = db.rawQuery(query, arrayOf(mesStr, anioStr))
        val resultado = mutableMapOf<Categoria, Double>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                resultado[Categoria(id, nombre)] = total
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return resultado
    }

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

    fun obtenerCategorias(): List<Categoria> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre FROM Categorias", null)
        val categorias = mutableListOf<Categoria>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                categorias.add(Categoria(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categorias
    }
}
