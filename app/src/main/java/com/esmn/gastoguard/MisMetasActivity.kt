package com.esmn.gastoguard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.BD.FireStoreDAO
import com.esmn.gastoguard.beans.Gasto
import com.esmn.gastoguard.beans.Item
import com.esmn.gastoguard.beans.Meta
import com.esmn.gastoguard.rv.Adapters.ItemAdapter
import java.util.*
import kotlin.collections.ArrayList

class MisMetasActivity : AppCompatActivity() {
    var idItemSeleccionado = 0

    lateinit var itemAdapter: ItemAdapter

    lateinit var metas: List<Item>
    lateinit var listaMetas: ArrayList<Meta>
    lateinit var idUsuario: String
    val dao: FireStoreDAO = FireStoreDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_metas)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }

        val header = findViewById<ConstraintLayout>(R.id.header)
        val searchBar = header.findViewById<ConstraintLayout>(R.id.search_bar)
        val searchEditText: EditText = searchBar.findViewById(R.id.search_edit_text)
        val searchButton: ImageButton = searchBar.findViewById(R.id.search_button)


        val logout: ImageButton = findViewById(R.id.logout_button)
        logout.setOnClickListener {
            dao.logoutUsuario()
            startActivity(Intent(this, MainActivity::class.java))
        }

        val editext1 = findViewById<EditText>(R.id.edittext1)
        editext1.hint = "Nombre..."
        val editext2 = findViewById<EditText>(R.id.edittext2)
        editext2.hint = "Valor Meta..."

        val combobox = findViewById<Spinner>(R.id.spinner)
        val entries = resources.getStringArray(R.array.options_metas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combobox.adapter = adapter
        combobox.setSelection(0)


        idUsuario = intent.getStringExtra("idUsuario")!!
        listaMetas =
            intent.getParcelableArrayListExtra<Meta>("listaMetas")!!
        val rvMetas: RecyclerView = findViewById(R.id.rv_metas)
        metas = toItem(listaMetas)
        initRecyclerView(metas, rvMetas)

        searchButton.setOnClickListener {
            filterRecyclerView(searchEditText.text.toString(), rvMetas)
        }


        val addBtn: ImageButton = findViewById(R.id.add_button_header)
        addBtn.setOnClickListener {
            addItem(rvMetas)
        }


    }

    override fun onResume() {
        super.onResume()

        listaMetas =
            intent.getParcelableArrayListExtra<Meta>("listaMetas")!!
        val rvMetas: RecyclerView = findViewById(R.id.rv_metas)
        metas = toItem(listaMetas)
        initRecyclerView(metas, rvMetas)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_item -> {
                idItemSeleccionado = itemAdapter.indiceSeleccionado
                eliminarMeta()
                return true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun eliminarMeta() {
        val metaIndex = idItemSeleccionado

        val metaId = listaMetas!![metaIndex].id!!

        dao.eliminarMeta(
            idUsuario, metaId,
            onSuccess = {
                Toast.makeText(this, "Meta eliminada con exito", Toast.LENGTH_SHORT)
                    .show()
                listaMetas =
                    listaMetas?.filterIndexed { index, _ -> index != metaIndex } as ArrayList<Meta>
                val rvMetas: RecyclerView = findViewById(R.id.rv_metas)
                metas = toItem(listaMetas)
                initRecyclerView(metas, rvMetas)
            },
            onFailure = { error ->
                Toast.makeText(
                    this,
                    "Error al eliminar el gasto: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun filterRecyclerView(query: String, recyclerView: RecyclerView) {
        val filteredGastos = metas.filter { meta ->
            meta.nombre.contains(query, ignoreCase = true)
        }
        val adapter = recyclerView.adapter as ItemAdapter
        adapter.items = filteredGastos
        adapter.notifyDataSetChanged()
    }

    fun addItem(recyclerView: RecyclerView) {
        val nombre: String = findViewById<EditText>(R.id.edittext1).text.toString()
        val monto = findViewById<EditText>(R.id.edittext2).text.toString()
        val tipo = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        val newMeta = Meta(nombre, monto.toDouble(), tipo)
        dao.agregarMeta(idUsuario, newMeta,
            onSuccess = {
                Toast.makeText(
                    this, "${newMeta.nombre} Se registro existosamente", Toast.LENGTH_SHORT
                ).show()

                val newList = metas as ArrayList<Item>
                newList.add(Item(newMeta.nombre, newMeta.monto, newMeta.tipo))
                val adapter = recyclerView.adapter as ItemAdapter
                adapter.notifyDataSetChanged()

                findViewById<EditText>(R.id.edittext1).setText("")
                findViewById<EditText>(R.id.edittext2).setText("")
                findViewById<Spinner>(R.id.spinner).setSelection(0)

            }, onFailure = { exception ->
                Toast.makeText(this, "Error agregar: $exception", Toast.LENGTH_SHORT).show()
            })

    }


    fun toItem(
        metas: ArrayList<Meta>
    ): List<Item> {
        return metas.map { meta -> Item(meta.nombre, meta.monto, meta.tipo) }
    }

    fun initRecyclerView(
        listItems: List<Item>, recyclerView: RecyclerView
    ) {
        val adapter = ItemAdapter(
            this, listItems
        )

        itemAdapter = adapter

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }
}