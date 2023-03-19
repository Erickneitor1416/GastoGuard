package com.esmn.gastoguard.BD

import android.util.Log
import com.esmn.gastoguard.beans.Gasto
import com.esmn.gastoguard.beans.Meta
import com.esmn.gastoguard.beans.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth


class FireStoreDAO {

    private val db = FirebaseFirestore.getInstance()

    // Obtener la referencia a Firebase Authentication
    private val auth = FirebaseAuth.getInstance()
    private val usuarioRef = db.collection("usuarios")

    // Crear una nueva playlist
    fun registrarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(usuario.username, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Agregar el usuario a Firestore
                    usuarioRef.add(usuario)
                        .addOnSuccessListener { documentReference ->
                            // Obtener referencia a las subcolecciones dentro del usuario creado
                            val gastosRef = documentReference.collection("gastos")
                            val metasRef = documentReference.collection("metas")

                            // Agregar cada gasto a la subcolección "gastos" dentro del usuario
                            for (gasto in usuario.gastos) {
                                gastosRef.add(gasto)
                                    .addOnSuccessListener {
                                        gasto.id = it.id
                                    }
                                    .addOnFailureListener { onFailure(it) }
                            }
                            // Agregar cada meta a la subcolección "metas" dentro del usuario
                            for (meta in usuario.metas) {
                                metasRef.add(meta)
                                    .addOnSuccessListener {
                                        meta.id = it.id
                                    }
                                    .addOnFailureListener { onFailure(it) }
                            }
                            usuario.id = documentReference.id
                            onSuccess()
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(task.exception!!)
                }
            }

    }

    // Obtener una lista de categorías con los gastos de un usuario
    fun getCategoriasDeUsuario(
        idUsuario: String,
        onSuccess: (Map<String, List<Gasto>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val gastosRef = usuarioRef.document(idUsuario).collection("gastos")

        gastosRef.get()
            .addOnSuccessListener { result ->
                val categorias = mutableMapOf<String, MutableList<Gasto>>()

                for (document in result) {
                    val gasto = document.toObject(Gasto::class.java)
                    gasto.id = document.id

                    if (categorias.containsKey(gasto.categoria)) {
                        categorias[gasto.categoria]?.add(gasto)
                    } else {
                        categorias[gasto.categoria] = mutableListOf(gasto)
                    }
                }

                onSuccess(categorias)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun loginUsuario(
        usuario: Usuario, onSuccess: (usuario: Usuario?) -> Unit, onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(usuario.username, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getUsuario(usuario, { usuarioCompleto ->
                        Log.e("usuario", usuarioCompleto.toString())
                        onSuccess(usuarioCompleto)
                    }, { exception ->
                        onFailure(exception)
                    })
                } else {
                    onFailure(task.exception!!)
                }
            }
    }

    fun logoutUsuario() {
        auth.signOut()
    }


    // Obtener un usuario
    fun getUsuario(
        usuario: Usuario, onSuccess: (Usuario?) -> Unit, onFailure: (Exception) -> Unit
    ) {
        usuarioRef.whereEqualTo("username", usuario.username)
            .whereEqualTo("password", usuario.password).get().addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    val newUsuario = result.documents[0].toObject(Usuario::class.java)
                    newUsuario?.id = result.documents[0].id
                    getGastosDeUsuario(newUsuario!!.id!!, { gastos ->
                        newUsuario.gastos = gastos.toList()
                        getMetasDeUsuario(newUsuario.id!!, { metas ->
                            newUsuario.metas = metas.toList()
                            onSuccess(newUsuario)
                        }, { exception ->
                            Log.e("TAG", "Error al obtener el usuario", exception)
                            onFailure(exception)
                        })
                    }, { exception ->
                        Log.e("TAG", "Error al obtener el usuario", exception)
                        onFailure(exception)
                    })


                }
            }.addOnFailureListener { exception ->
                Log.e("TAG", "Error al obtener el usuario", exception)
                onFailure(exception)
            }
    }


    // Obtener todas los gastos de un usuario
    fun getGastosDeUsuario(
        idUsuario: String,
        onSuccess: (List<Gasto>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val gastosRef = usuarioRef.document(idUsuario).collection("gastos")

        gastosRef.get()
            .addOnSuccessListener { result ->
                val gastos = mutableListOf<Gasto>()
                for (document in result) {
                    val gasto = document.toObject(Gasto::class.java)
                    gasto.id = document.id
                    gastos.add(gasto)
                }
                Log.e("gastosssss424524", gastos.toString())
                onSuccess(gastos)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // Obtener todas los gastos de un usuario
    fun getMetasDeUsuario(
        idUsuario: String,
        onSuccess: (List<Meta>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val metasRef = usuarioRef.document(idUsuario).collection("metas")

        metasRef.get()
            .addOnSuccessListener { result ->
                val metas = mutableListOf<Meta>()
                for (document in result) {
                    val meta = document.toObject(Meta::class.java)
                    meta.id = document.id
                    metas.add(meta)
                }
                Log.e("gastosssss424524", metas.toString())
                onSuccess(metas)
            }
            .addOnFailureListener { onFailure(it) }
    }


    fun agregarGasto(
        idUsuario: String,
        gasto: Gasto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtener la referencia a la subcolección "gastos" del usuario
        val gastosRef = usuarioRef.document(idUsuario).collection("gastos")

        // Agregar el gasto a la subcolección
        gastosRef.add(gasto).addOnSuccessListener {
            // Obtener el id generado por Firestore
            gasto.id = it.id
            onSuccess()
        }
            .addOnFailureListener { onFailure(it) }
    }


    fun agregarMeta(
        idUsuario: String,
        meta: Meta,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtener la referencia a la subcolección "gastos" del usuario
        val gastosRef = usuarioRef.document(idUsuario).collection("metas")

        // Agregar el gasto a la subcolección
        gastosRef.add(meta).addOnSuccessListener {
            // Obtener el id generado por Firestore
            meta.id = it.id
            onSuccess()
        }.addOnFailureListener { onFailure(it) }
    }

    fun eliminarGasto(
        idUsuario: String,
        idGasto: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtener referencia al documento de la canción dentro de la subcolección
        val gastoRef =
            usuarioRef.document(idUsuario).collection("gastos").document(idGasto)

        gastoRef.get()
            .addOnSuccessListener { gastoDoc ->
                // Verificar que el documento exista antes de eliminarlo
                if (gastoDoc.exists()) {
                    // Eliminar el documento de la subcolección
                    gastoRef.delete()
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("No se encontró el gasto con el ID $idGasto"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun eliminarMeta(
        idUsuario: String,
        idMeta: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtener referencia al documento de la canción dentro de la subcolección
        val metaRef =
            usuarioRef.document(idUsuario).collection("metas").document(idMeta)

        metaRef.get()
            .addOnSuccessListener { metaDoc ->
                // Verificar que el documento exista antes de eliminarlo
                if (metaDoc.exists()) {
                    // Eliminar el documento de la subcolección
                    metaRef.delete()
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("No se encontró la meta con el ID $idMeta"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }


}
