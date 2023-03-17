package com.esmn.gastoguard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmn.gastoguard.BD.FireStoreDAO
import com.esmn.gastoguard.beans.Categoria
import com.esmn.gastoguard.beans.Gasto
import com.esmn.gastoguard.beans.Meta
import com.esmn.gastoguard.beans.Usuario
import com.esmn.gastoguard.rv.Adapters.ResumenItemAdapter

class MainResumenActivity : AppCompatActivity() {
    private val dao : FireStoreDAO = FireStoreDAO()
    lateinit var usuario: Usuario
    lateinit var resumenRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_resumen)

        val logout: ImageButton = findViewById(R.id.logout_button)
        logout.setOnClickListener {
            dao.logoutUsuario()
            startActivity(Intent(this, MainActivity::class.java))
        }


        resumenRecyclerView = findViewById<RecyclerView>(R.id.resumen_rv)
        usuario = intent.getParcelableExtra("usuario")?: Usuario()


        cargarDatos(usuario,resumenRecyclerView)


        val addButton = findViewById<ImageButton>(R.id.add_button)
        val popupMenu = PopupMenu(this, addButton)
        popupMenu.inflate(R.menu.menu_contextual_resumen)
        addButton.setOnClickListener{
            popupMenu.show()
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_gasto -> {
                    val intent = Intent(this, MisGastosActivity::class.java)
                    val listaGastos = usuario.gastos as ArrayList<Gasto>
                    intent.putExtra("idUsuario",usuario.id)
                    intent.putParcelableArrayListExtra("listaGastos", listaGastos)
                    startActivity(intent)
                    true
                }
                R.id.menu_add_meta -> {
                    val intent = Intent(this, MisMetasActivity::class.java)
                    val listaMetas = usuario.metas as ArrayList<Meta>
                    intent.putExtra("idUsuario",usuario.id)
                    intent.putParcelableArrayListExtra("listaMetas", listaMetas)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



    }

    override fun onResume() {
        super.onResume()
        cargarDatos(usuario,resumenRecyclerView)
    }
    private fun cargarDatos(usuario: Usuario, recyclerView: RecyclerView){
        dao.getCategoriasDeUsuario(usuario.id!!,
            onSuccess = { categorias ->
                val resumenList = mutableListOf<Categoria>()
                for ((categoria, gastos) in categorias) {
                    Log.e("catgoria", categoria)
                    resumenList.add(Categoria(categoria, gastos))
                }

                val resumenAdapter = ResumenItemAdapter(resumenList)
                recyclerView.adapter = resumenAdapter
            },
            onFailure = { error ->
                Toast.makeText(this, "Error al obtener las categorías: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
