package com.nachofm.evl.vet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vet")
data class Vet(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "email_address") val emailAddress: String?
)
