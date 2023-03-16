package com.esmn.gastoguard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.beans.Item
import com.esmn.gastoguard.rv.Adapters.ItemAdapter

class MisMetasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_metas)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val editext1 = findViewById<EditText>(R.id.edittext1)
        editext1.hint = "Nombre.."
        val editext2 = findViewById<EditText>(R.id.edittext2)
        editext2.hint = "Valor Meta.."

        val combobox = findViewById<Spinner>(R.id.spinner)
        val entries = resources.getStringArray(R.array.options_metas)
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combobox.adapter = adapter
        combobox.setSelection(0)
    }
    fun initRecyclerView(
        listItems: ArrayList<Item>,
        recyclerView: RecyclerView
    ){
        val adapter = ItemAdapter(
            this,
            listItems
        )
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }
}