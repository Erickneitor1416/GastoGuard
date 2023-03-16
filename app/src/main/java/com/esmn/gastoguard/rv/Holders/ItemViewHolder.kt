package com.esmn.gastoguard.rv.Holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.R

class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val nombre: TextView = itemView.findViewById(R.id.nombre_textview)
    val monto: TextView = itemView.findViewById(R.id.monto_textview)
    val fecha: TextView = itemView.findViewById(R.id.fecha_textview)

}