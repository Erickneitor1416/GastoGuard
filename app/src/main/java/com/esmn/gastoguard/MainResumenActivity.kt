package com.esmn.gastoguard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_resumen)

        val resumenRecyclerView = findViewById<RecyclerView>(R.id.resumen_rv)
        val usuario: Usuario = intent.getParcelableExtra<Usuario>("usuario")?: Usuario()
        dao.getCategoriasDeUsuario(usuario.id!!,
            onSuccess = { categorias ->
                val resumenList = mutableListOf<Categoria>()
                for ((categoria, gastos) in categorias) {
                    resumenList.add(Categoria(categoria, gastos))
                }

                val resumenAdapter = ResumenItemAdapter(resumenList)
                resumenRecyclerView.adapter = resumenAdapter
            },
            onFailure = { error ->
                Toast.makeText(this, "Error al obtener las categorías: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        resumenRecyclerView.layoutManager = LinearLayoutManager(this)


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
                    intent.putParcelableArrayListExtra("listaGastos", listaGastos)
                    startActivity(intent)
                    true
                }
                R.id.menu_add_meta -> {
                    val intent = Intent(this, MisMetasActivity::class.java)
                    val listaMetas = usuario.metas as ArrayList<Meta>
                    intent.putParcelableArrayListExtra("listaMetas", listaMetas)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



    }
}
