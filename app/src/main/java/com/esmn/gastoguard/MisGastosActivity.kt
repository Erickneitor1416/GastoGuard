package com.esmn.gastoguard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.BD.FireStoreDAO
import com.esmn.gastoguard.beans.Gasto
import com.esmn.gastoguard.beans.Item
import com.esmn.gastoguard.rv.Adapters.ItemAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MisGastosActivity : AppCompatActivity() {
    var idItemSeleccionado = 0

    lateinit var itemAdapter: ItemAdapter

    lateinit var gastos: List<Item>
    lateinit var listaGastos: ArrayList<Gasto>
    lateinit var idUsuario: String
    val dao: FireStoreDAO = FireStoreDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_gastos)
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
        editext1.hint = "Descripcion..."
        val editext2 = findViewById<EditText>(R.id.edittext2)
        editext2.hint = "Monto..."

        val combobox = findViewById<Spinner>(R.id.spinner)
        val entries = resources.getStringArray(R.array.options_gastos)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combobox.adapter = adapter
        combobox.setSelection(0)

        idUsuario = intent.getStringExtra("idUsuario")!!
        listaGastos =
            intent.getParcelableArrayListExtra<Gasto>("listaGastos")!!
        val rvGastos: RecyclerView = findViewById(R.id.rv_gastos)
        gastos = toItem(listaGastos)
        initRecyclerView(gastos, rvGastos)

        searchButton.setOnClickListener {
            filterRecyclerView(searchEditText.text.toString(), rvGastos)
        }


        val addBtn: ImageButton = findViewById(R.id.add_button_header)
        addBtn.setOnClickListener {
            addItem(rvGastos)
        }


    }

    override fun onResume() {
        super.onResume()

        listaGastos =
            intent.getParcelableArrayListExtra<Gasto>("listaGastos")!!
        val rvGastos: RecyclerView = findViewById(R.id.rv_gastos)
        gastos = toItem(listaGastos)
        initRecyclerView(gastos, rvGastos)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_item -> {
                idItemSeleccionado = itemAdapter.indiceSeleccionado
                eliminarGasto()
                return true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun eliminarGasto() {
        val gastoIndex = idItemSeleccionado

        val gastoId = listaGastos!![gastoIndex].id!!

        dao.eliminarGasto(
            idUsuario, gastoId,
            onSuccess = {
                Toast.makeText(this, "Gasto eliminado con exito", Toast.LENGTH_SHORT)
                    .show()
                listaGastos =
                    listaGastos?.filterIndexed { index, _ -> index != gastoIndex } as ArrayList<Gasto>
                val rvGastos: RecyclerView = findViewById(R.id.rv_gastos)
                gastos = toItem(listaGastos)
                initRecyclerView(gastos, rvGastos)
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
        val filteredGastos = gastos.filter { gasto ->
            gasto.nombre.contains(query, ignoreCase = true)
        }
        val adapter = recyclerView.adapter as ItemAdapter
        adapter.items = filteredGastos
        adapter.notifyDataSetChanged()
    }


    fun addItem(recyclerView: RecyclerView) {
        val descripcion: String = findViewById<EditText>(R.id.edittext1).text.toString()
        val monto = findViewById<EditText>(R.id.edittext2).text.toString()
        val categoria = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formatoFecha.format(Date())
        val newGasto = Gasto(descripcion, monto.toDouble(), fechaActual, categoria)

        dao.agregarGasto(idUsuario, newGasto,
            onSuccess = {
                Toast.makeText(
                    this,
                    "${newGasto.descripcion} Se registro existosamente",
                    Toast.LENGTH_SHORT
                )
                    .show()

                val newList = gastos as ArrayList<Item>
                newList.add(Item(newGasto.descripcion, newGasto.monto, newGasto.fecha))
                listaGastos.add(newGasto)
                gastos = newList
                val adapter = recyclerView.adapter as ItemAdapter
                adapter.notifyDataSetChanged()

                findViewById<EditText>(R.id.edittext1).setText("")
                findViewById<EditText>(R.id.edittext2).setText("")
                findViewById<Spinner>(R.id.spinner).setSelection(0)

            }, onFailure = { exception ->
                Toast.makeText(this, "Error agregar: $exception", Toast.LENGTH_SHORT)
                    .show()
            })

    }


    fun toItem(
        gastos: ArrayList<Gasto>
    ): List<Item> {
        return gastos.map { gasto -> Item(gasto.descripcion, gasto.monto, gasto.fecha) }
    }

    fun initRecyclerView(
        listItems: List<Item>,
        recyclerView: RecyclerView
    ) {
        val adapter = ItemAdapter(
            this,
            listItems
        )

        itemAdapter = adapter

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }
}