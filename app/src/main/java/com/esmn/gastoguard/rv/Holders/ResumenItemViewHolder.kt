package com.esmn.gastoguard.rv.Holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.R

class ResumenItemViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    val categoria: TextView = itemView.findViewById(R.id.categoria)
    val valorCategoria: TextView = itemView.findViewById(R.id.valorCategoria)
    val recyclerViewHijo: RecyclerView = itemView.findViewById(R.id.recycler_gastos)
}