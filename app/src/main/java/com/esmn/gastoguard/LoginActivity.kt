package com.esmn.gastoguard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.esmn.gastoguard.BD.FireStoreDAO
import com.esmn.gastoguard.beans.Usuario

class LoginActivity : AppCompatActivity() {
    private val dao: FireStoreDAO = FireStoreDAO()
    lateinit var usuario: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnRegistrar = findViewById<TextView>(R.id.textView)
        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, MainResumenActivity::class.java))
        }
        val btnIngresar = findViewById<Button>(R.id.id_login_button)
        btnIngresar.setOnClickListener {
            val username = findViewById<EditText>(R.id.id_username)
            val password = findViewById<EditText>(R.id.id_password)
            usuario = Usuario(username.text.toString(), password.text.toString())
            dao.loginUsuario(usuario, {
                usuario = it!!
                Toast.makeText(
                    this,
                    "${usuario.username} Inicio sesion existosamente",
                    Toast.LENGTH_SHORT
                )
                    .show()
                val intent = Intent(this, MainResumenActivity::class.java)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            }, { exception ->
                // Manejar el error si ocurre
                Toast.makeText(this, "Error al iniciar Sesion: $exception", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }
}