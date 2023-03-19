package com.esmn.gastoguard.rv.Adapters

import android.content.Context
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.esmn.gastoguard.beans.Item
import com.esmn.gastoguard.rv.Holders.ItemViewHolder
import com.esmn.gastoguard.R

class ItemAdapter(
    private val context: Context,
    var items: List<Item>
) : RecyclerView.Adapter<ItemViewHolder>() {

    var indiceSeleccionado = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_rv, parent, false)

        return ItemViewHolder(itemView).apply {
            itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                Log.e("indice seleccionado", adapterPosition.toString())
                indiceSeleccionado = adapterPosition
                val activityContext = context as AppCompatActivity
                activityContext.menuInflater.inflate(R.menu.menu_contextual_items, menu)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nombre.text = item.nombre
        holder.monto.text = "$" + item.monto.toString()
        holder.fecha.text = item.fecha
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
