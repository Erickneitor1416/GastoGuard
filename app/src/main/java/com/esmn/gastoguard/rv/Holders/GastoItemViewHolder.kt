package com.esmn.gastoguard.rv.Holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.R

class GastoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val fechaGasto: TextView = itemView.findViewById(R.id.fecha_gasto)
    val valor: TextView = itemView.findViewById(R.id.valor_gasto)


}