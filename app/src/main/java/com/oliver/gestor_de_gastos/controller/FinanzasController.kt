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

    fun obtenerSaldoActual(): Int {
        val db = dbHelper.readableDatabase
        val cursorIngresos = db.rawQuery("SELECT SUM(Monto) FROM Ingresos", null)
        val cursorGastos = db.rawQuery("SELECT SUM(Monto) FROM Gastos", null)
        var totalIngresos = 0
        var totalGastos = 0
        if (cursorIngresos.moveToFirst()) totalIngresos = cursorIngresos.getInt(0)
        if (cursorGastos.moveToFirst()) totalGastos = cursorGastos.getInt(0)
        cursorIngresos.close()
        cursorGastos.close()
        db.close()
        return totalIngresos - totalGastos
    }

    fun obtenerGastoTotal(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT SUM(Monto) FROM Gastos", null)
        var total = 0
        if (cursor.moveToFirst()) total = cursor.getInt(0)
        cursor.close()
        db.close()
        return total
    }

    fun registrarIngreso(monto: Int, descripcion: String = "Ingreso pasivo") {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO Transacciones (name) VALUES ('Ingreso pasivo')")
        val cursor = db.rawQuery("SELECT id FROM Transacciones ORDER BY id DESC LIMIT 1", null)
        var idTrans = 1
        if (cursor.moveToFirst()) idTrans = cursor.getInt(0)
        cursor.close()
        db.execSQL("INSERT INTO Ingresos (id_transaction, Description, Monto) VALUES ($idTrans, '$descripcion', $monto)")
        db.close()
    }

    fun registrarGasto(idCategoria: Int, monto: Int, descripcion: String = "Gasto registrado") {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO Transacciones (name) VALUES ('Gasto registrado')")
        val cursor = db.rawQuery("SELECT id FROM Transacciones ORDER BY id DESC LIMIT 1", null)
        var idTrans = 1
        if (cursor.moveToFirst()) idTrans = cursor.getInt(0)
        cursor.close()
        db.execSQL("INSERT INTO Gastos (id_categoria, id_transaction, Description, Monto) VALUES ($idCategoria, $idTrans, '$descripcion', $monto)")
        db.close()
    }
}

