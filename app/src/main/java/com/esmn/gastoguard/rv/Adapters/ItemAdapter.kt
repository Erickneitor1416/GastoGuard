package com.esmn.gastoguard.rv.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.beans.Item
import com.esmn.gastoguard.rv.Holders.ItemViewHolder
import com.esmn.gastoguard.R

class ItemAdapter (
    private val context: Context,
    private val items: List<Item>
    ) : RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_rv, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nombre.text = item.nombre
        holder.monto.text = item.monto.toString()
        holder.fecha.text = item.fecha
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
