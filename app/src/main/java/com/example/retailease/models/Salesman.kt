package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "salesmen")
data class Salesman (
    @PrimaryKey(autoGenerate = true)
    val salesmanId: Int = 0,
    val name: String,
    val imagePath: String? = null,
    val phoneNumber: String? = null,
    val discount: Double = 0.0
)
