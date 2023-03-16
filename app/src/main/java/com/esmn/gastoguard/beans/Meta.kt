package com.esmn.gastoguard.beans

import android.os.Parcel
import android.os.Parcelable
import com.esmn.gastoguard.beans.Recordatorio
import com.google.firebase.firestore.Exclude

data class Meta(
    @Exclude @JvmField var id: String?, // Se excluye la propiedad "id" de Firestore
    val nombre: String,
    val monto: String,
    var finalfecha: String,
    //@Exclude @JvmField var recordatorios: List<Recordatorio> = emptyList()
) : Parcelable {

    // Constructor vacío para usar con Firebase
    constructor() : this(null, "", "", "")

    // Constructor vacío para objetos
    constructor(nombre: String, monto: String, finalfecha: String)
            : this(null, nombre, monto, finalfecha)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
       /* mutableListOf<Recordatorio>().apply {
            parcel.readList(this, Recordatorio::class.java.classLoader)
        }*/
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeString(monto)
        parcel.writeString(finalfecha)
        //parcel.writeList(recordatorios)
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
