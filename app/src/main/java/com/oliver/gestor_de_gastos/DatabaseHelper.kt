package com.oliver.gestor_de_gastos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //Se llamará cuando la base de datos no exista
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Categorias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL
            );
        """)
        db.execSQL("""
            CREATE TABLE Transacciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
        """)
        db.execSQL("""
            CREATE TABLE Gastos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_categoria INTEGER NOT NULL,
                id_transaction INTEGER NOT NULL,
                Description TEXT NOT NULL,
                Fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,
                Monto INTEGER NOT NULL CHECK(Monto >= 0),
                FOREIGN KEY (id_categoria) REFERENCES Categorias(id) ON DELETE CASCADE,
                FOREIGN KEY (id_transaction) REFERENCES Transacciones(id) ON DELETE CASCADE
            );
        """)
        db.execSQL("""
            CREATE TABLE Ingresos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_transaction INTEGER NOT NULL,
                Description TEXT NOT NULL,
                fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,
                Monto INTEGER NOT NULL CHECK(Monto >= 0),
                FOREIGN KEY (id_transaction) REFERENCES Transacciones(id) ON DELETE CASCADE
            );
        """)
        db.execSQL("CREATE INDEX idx_gastos_categoria ON Gastos(id_categoria);")
        db.execSQL("CREATE INDEX idx_gastos_transaccion ON Gastos(id_transaction);")
        db.execSQL("CREATE INDEX idx_ingresos_transaccion ON Ingresos(id_transaction);")
    }

    //resetear la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Ingresos")
        db.execSQL("DROP TABLE IF EXISTS Gastos")
        db.execSQL("DROP TABLE IF EXISTS Transacciones")
        db.execSQL("DROP TABLE IF EXISTS Categorias")
        onCreate(db)
    }

    companion object {
        fun onUpgrade() {
            this.onUpgrade()
        }

        private const val DATABASE_NAME = "gestor_gastos.db"
        //version 1, se pueden agregar nuevas tablas o modificar las existentes en futuras versiones
        private const val DATABASE_VERSION = 1
    }
}

