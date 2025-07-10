package com.oliver.gestor_de_gastos.controller

import android.content.Context
import com.oliver.gestor_de_gastos.DatabaseHelper
import com.oliver.gestor_de_gastos.model.Categoria
import com.oliver.gestor_de_gastos.model.Gasto
import com.oliver.gestor_de_gastos.model.Ingreso

class FinanzasController(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun obtenerCategorias(): List<Categoria> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre FROM Categorias", null)
        val categorias = mutableListOf<Categoria>()
        while (cursor.moveToNext()) {
            categorias.add(Categoria(cursor.getInt(0), cursor.getString(1)))
        }
        cursor.close()
        db.close()
        return categorias
    }

    fun obtenerSaldoActual(): Double {
        val db = dbHelper.readableDatabase
        val cursorIngresos = db.rawQuery("SELECT SUM(Monto) FROM Ingresos", null)
        val cursorGastos = db.rawQuery("SELECT SUM(Monto) FROM Gastos", null)
        var totalIngresos = 0.0
        var totalGastos = 0.0
        if (cursorIngresos.moveToFirst()) totalIngresos = cursorIngresos.getDouble(0)
        if (cursorGastos.moveToFirst()) totalGastos = cursorGastos.getDouble(0)
        cursorIngresos.close()
        cursorGastos.close()
        db.close()
        return totalIngresos - totalGastos
    }

    fun obtenerGastoTotal(): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT SUM(Monto) FROM Gastos", null)
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        db.close()
        return total
    }

    fun registrarIngreso(monto: Double, descripcion: String = "Ingreso pasivo") {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO Transacciones (tipo, descripcion) VALUES (?, ?)", arrayOf("INGRESO", descripcion))
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var transId = 0
        if (cursor.moveToFirst()) transId = cursor.getInt(0)
        cursor.close()
        db.execSQL("INSERT INTO Ingresos (id_transaction, Description, Monto) VALUES (?, ?, ?)", arrayOf(transId, descripcion, monto))
        db.close()
    }

    fun registrarGasto(idCategoria: Int, monto: Double, descripcion: String = "Gasto") {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO Transacciones (tipo, descripcion) VALUES (?, ?)", arrayOf("GASTO", descripcion))
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var transId = 0
        if (cursor.moveToFirst()) transId = cursor.getInt(0)
        cursor.close()
        db.execSQL("INSERT INTO Gastos (id_categoria, id_transaction, Description, Monto) VALUES (?, ?, ?, ?)", arrayOf(idCategoria, transId, descripcion, monto))
        db.close()
    }

    fun obtenerRegistrosPorCategoria(idCategoria: Int): List<Gasto> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, id_categoria, id_transaction, Description, Fecha_registro, Monto FROM Gastos WHERE id_categoria = ?", arrayOf(idCategoria.toString()))
        val gastos = mutableListOf<Gasto>()
        while (cursor.moveToNext()) {
            gastos.add(
                Gasto(
                    id = cursor.getInt(0),
                    idCategoria = cursor.getInt(1),
                    idTransaccion = cursor.getInt(2),
                    descripcion = cursor.getString(3),
                    fecha = cursor.getString(4),
                    monto = cursor.getInt(5)
                )
            )
        }
        cursor.close()
        db.close()
        return gastos
    }

    fun editarGasto(id: Int, nuevaDescripcion: String, nuevoMonto: Double) {
        val db = dbHelper.writableDatabase
        db.execSQL("UPDATE Gastos SET Description = ?, Monto = ? WHERE id = ?", arrayOf(nuevaDescripcion, nuevoMonto, id))
        db.close()
    }

    fun eliminarGasto(id: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM Gastos WHERE id = ?", arrayOf(id))
        db.close()
    }
}
