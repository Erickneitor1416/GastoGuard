package com.esmn.gastoguard.beans

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class Gasto(
    @Exclude @JvmField var id: String?, // Se excluye la propiedad "id" de Firestore
    val descripcion: String,
    val monto: Double,
    var fecha: String,
    var categoria: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    // Constructor vacío para usar con Firebase
    constructor() : this(null, "", 0.0, "", "")

    // Constructor vacío para objetos
    constructor(descripcion: String, monto: Double, fecha: String, categoria: String)
            : this(null, descripcion, monto, fecha, categoria)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(descripcion)
        parcel.writeDouble(monto)
        parcel.writeString(fecha)
        parcel.writeString(categoria)
    }

    companion object CREATOR : Parcelable.Creator<Gasto> {
        override fun createFromParcel(parcel: Parcel): Gasto {
            return Gasto(parcel)
        }

        override fun newArray(size: Int): Array<Gasto?> {
            return arrayOfNulls(size)
        }
    }

}