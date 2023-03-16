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


    /*// Actualizar una playlist existente
    fun actualizarPlaylist(
        idPlaylist: String,
        playlist: PlayList,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuarioRef.document(idPlaylist).set(playlist)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }*/

    /*// Eliminar una playlist existente
    fun eliminarPlaylist(
        idPlaylist: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuarioRef.document(idPlaylist).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }*/

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


    // Obtener un usuario
    fun getUsuario(
        usuario: Usuario, onSuccess: (Usuario?) -> Unit, onFailure: (Exception) -> Unit
    ) {
        usuarioRef.whereEqualTo("username", usuario.username)
            .whereEqualTo("password", usuario.password).get().addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    val usuario = result.documents[0].toObject(Usuario::class.java)
                    usuario?.id = result.documents[0].id
                    getGastosDeUsuario(usuario!!.id!!, { gastos ->
                        usuario.gastos = gastos.toList()
                    }, { exception ->
                        Log.e("TAG", "Error al obtener el usuario", exception)
                        onFailure(exception)
                    })

                    getMetasDeUsuario(usuario.id!!, { metas ->
                        usuario.metas = metas.toList()
                    }, { exception ->
                        Log.e("TAG", "Error al obtener el usuario", exception)
                        onFailure(exception)
                    })
                    onSuccess(usuario)
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
                onSuccess(metas)
            }
            .addOnFailureListener { onFailure(it) }
    }

    /* Agregar una nueva canción a una playlist existente
    fun agregarCancion(
        idPlaylist: String,
        cancion: Cancion,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Obtener referencia a la playlist
        val playlistRef = playlistsRef.document(idPlaylist)
        playlistRef.get()
            .addOnSuccessListener { playlistDoc ->
                // Obtener matriz de canciones y asignar un nuevo ID a la nueva canción
                val canciones = playlistDoc.get("canciones") as? List<Cancion> ?: emptyList()


                // Agregar la nueva canción a la matriz de canciones
                val nuevasCanciones = canciones + cancion

                // Actualizar el campo canciones en Firestore con la nueva matriz
                playlistRef.update("canciones", nuevasCanciones)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }*/

    /* // Actualizar una canción existente dentro de una playlist
     fun actualizarCancion(idPlaylist: String, cancion: Cancion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
         val cancionesRef = usuarioRef.document(idPlaylist).collection("canciones")
         cancionesRef.document(cancion.id!!).set(cancion)
             .addOnSuccessListener { onSuccess() }
             .addOnFailureListener { onFailure(it) }
     }

     // Actualizar una canción existente dentro de una playlist
     fun agregarCancion(idPlaylist: String, cancion: Cancion, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
         val cancionesRef = usuarioRef.document(idPlaylist).collection("canciones")
         cancionesRef.add(cancion)
             .addOnSuccessListener {
                 cancion.id = it.id
                 onSuccess()
             }
             .addOnFailureListener { onFailure(it) }
     }

     // Eliminar una canción existente dentro de una playlist
     fun eliminarCancion(
         idPlaylist: String,
         idCancion: String,
         onSuccess: () -> Unit,
         onFailure: (Exception) -> Unit
     ) {
         // Obtener referencia al documento de la canción dentro de la subcolección
         val cancionRef =
             usuarioRef.document(idPlaylist).collection("canciones").document(idCancion)

         cancionRef.get()
             .addOnSuccessListener { cancionDoc ->
                 // Verificar que el documento exista antes de eliminarlo
                 if (cancionDoc.exists()) {
                     // Eliminar el documento de la subcolección
                     cancionRef.delete()
                         .addOnSuccessListener { onSuccess() }
                         .addOnFailureListener { onFailure(it) }
                 } else {
                     onFailure(Exception("No se encontró la canción con el ID $idCancion"))
                 }
             }
             .addOnFailureListener { onFailure(it) }
     }*/


}