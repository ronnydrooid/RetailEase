package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val customerId: Int = 0,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val loyaltyPoints: Double = 0.0,
    val totalPurchases: Double = 0.0
)
