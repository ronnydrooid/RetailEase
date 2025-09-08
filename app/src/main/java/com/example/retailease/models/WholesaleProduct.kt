package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.retailease.models.converters.BigDecimalSerializer
import com.example.retailease.models.converters.DateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.Date

@Serializable
@Entity(tableName = "wholesale_products")
data class WholesaleProduct(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,
    val productName: String,
    @Serializable(with = BigDecimalSerializer::class)
    val wholesalePrice200gm: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val wholesalePrice500gm: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val salesmanPrice200gm: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val salesmanPrice500gm: BigDecimal,
    val isSalesmanDiscountEnabled: Boolean = true,
    val imagePath: String? = null,
    val category: String? = null,
    @Serializable(with = DateSerializer::class)
    val lastUpdated: Date = Date()
)
