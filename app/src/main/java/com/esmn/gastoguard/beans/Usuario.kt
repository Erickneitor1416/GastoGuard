package com.esmn.gastoguard.beans

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

data class Usuario(
    @Exclude @JvmField  var id: String?, // Se excluye la propiedad "id" de Firestore
    val username: String,
    val password: String,
    @Exclude @JvmField var gastos: List<Gasto>,
    @Exclude @JvmField var metas: List<Meta>
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        mutableListOf<Gasto>().apply {
            parcel.readList(this, Gasto::class.java.classLoader)
        },
        mutableListOf<Meta>().apply {
            parcel.readList(this, Meta::class.java.classLoader)
        }
    ) {
    }

    // Constructor vacío para usar con Firebase
    constructor() : this(null, "", "", emptyList(), emptyList())
    // Constructor vacío para objetos
    constructor(username: String, password: String, gastos: List<Gasto>, metas: List<Meta>)
            : this(null, username, password, gastos, metas)
    constructor(username: String, password: String)
            : this(null, username, password, emptyList(), emptyList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeList(gastos)
        parcel.writeList(metas)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usuario> {
        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(parcel)
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
    }
}