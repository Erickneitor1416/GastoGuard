package com.esmn.gastoguard.beans

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

data class Meta(
    @Exclude @JvmField var id: String?, // Se excluye la propiedad "id" de Firestore
    val nombre: String,
    val monto: Double,
    var tipo: String,
) : Parcelable {

    // Constructor vacío para usar con Firebase
    constructor() : this(null, "", 0.0, "")

    // Constructor vacío para objetos
    constructor(nombre: String, monto: Double, finalfecha: String)
            : this(null, nombre, monto, finalfecha)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readDouble()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeDouble(monto)
        parcel.writeString(tipo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meta> {
        override fun createFromParcel(parcel: Parcel): Meta {
            return Meta(parcel)
        }

        override fun newArray(size: Int): Array<Meta?> {
            return arrayOfNulls(size)
        }
    }
}
