package com.esmn.gastoguard.rv.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.R
import com.esmn.gastoguard.rv.Holders.ResumenItemViewHolder
import com.esmn.gastoguard.beans.Categoria

class ResumenItemAdapter(private val resumenList: List<Categoria>) :
    RecyclerView.Adapter<ResumenItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumenItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_resumen, parent, false)
        return ResumenItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResumenItemViewHolder, position: Int) {
        val resumenItem = resumenList[position]
        holder.categoria.text = resumenItem.categoria
        holder.valorCategoria.text = resumenItem.valorCategoria.toString()
        // Crear un adaptador hijo con la lista de elementos de la categoría
        val elementoAdapter = GastoItemAdapter(resumenItem.listGastos)
        // Asignar un LinearLayoutManager horizontal al RecyclerView hijo
        holder.recyclerViewHijo.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        // Asignar el adaptador hijo al RecyclerView hijo
        holder.recyclerViewHijo.adapter = elementoAdapter
        holder.recyclerViewHijo.addItemDecoration(
            DividerItemDecoration(holder.recyclerViewHijo.context, DividerItemDecoration.VERTICAL)
        )
    }

    override fun getItemCount(): Int {
        return resumenList.size
    }
}