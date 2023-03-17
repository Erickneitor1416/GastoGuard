package com.esmn.gastoguard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.esmn.gastoguard.BD.FireStoreDAO
import com.esmn.gastoguard.beans.Usuario

class MainActivity : AppCompatActivity() {

    private val dao: FireStoreDAO = FireStoreDAO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = findViewById<TextView>(R.id.textView)
        login.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        val btnRegistrar = findViewById<Button>(R.id.id_login_button)
        btnRegistrar.setOnClickListener {
            val username = findViewById<EditText>(R.id.id_username)
            val password = findViewById<EditText>(R.id.id_password)
            val usuario = Usuario(username.text.toString(), password.text.toString())
            dao.registrarUsuario(usuario, {
                Toast.makeText(
                    this,
                    "${usuario.username} Se registro existosamente",
                    Toast.LENGTH_SHORT
                )
                    .show()
                startActivity(Intent(this, LoginActivity::class.java))
            }, { exception ->
                // Manejar el error si ocurre
                Toast.makeText(this, "Error registrarse: $exception", Toast.LENGTH_SHORT)
                    .show()
                Log.e("registrooo", exception.toString())
            })
        }


    }


}