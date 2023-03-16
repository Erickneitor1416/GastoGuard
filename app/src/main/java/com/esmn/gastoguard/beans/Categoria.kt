package com.esmn.gastoguard.beans

class Categoria(
    val categoria: String,
    val listGastos: List<Gasto>
) {
    var valorCategoria: Double = 0.0


    init {
        valorCategoria = listGastos.sumOf { it.monto }

    }


}