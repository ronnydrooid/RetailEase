package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)val employeeId: Int = 0,
    val name: String,
    val salary: Double,
    val phoneNumber: String,
    val email: String? = null,
    val department: String,
    val imageUrl: String? = null
)
