package com.esmn.gastoguard.rv.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.rv.Holders.GastoItemViewHolder
import com.esmn.gastoguard.R
import com.esmn.gastoguard.beans.Gasto

class GastoItemAdapter (
    private val items: List<Gasto>
)  : RecyclerView.Adapter<GastoItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.gasto_item,parent, false)

        return GastoItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoItemViewHolder, position: Int) {
        val item = items[position]
        holder.fechaGasto.text = item.fecha
        holder.valor.text = item.monto.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}