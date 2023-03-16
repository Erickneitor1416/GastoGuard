package com.esmn.gastoguard.beans

import com.google.firebase.firestore.Exclude

class Recordatorio(
    @Exclude @JvmField var id: String?, // Se excluye la propiedad "id" de Firestore
    var fecha: String,
    var mensaje: String
) {
    // Constructor vacío para usar con Firebase
    constructor() : this(null, "", "")
    // Constructor vacío para objetos
    constructor(fecha: String,mensaje: String)
            : this(null, fecha,mensaje)
}