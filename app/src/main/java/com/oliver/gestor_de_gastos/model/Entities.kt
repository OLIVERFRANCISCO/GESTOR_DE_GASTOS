package com.oliver.gestor_de_gastos.model


data class Categoria(val id: Int,
                     val nombre: String)

data class Gasto(val id: Int,
                 val idCategoria: Int,
                 val idTransaccion: Int,
                 val descripcion: String,
                 val fecha: String,
                 val monto: Int)


data class Ingreso(val id: Int,
                   val idTransaccion: Int,
                   val descripcion: String,
                   val fecha: String,
                   val monto: Int)

