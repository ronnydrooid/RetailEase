package com.example.retailease.backup

import com.example.retailease.models.Product
import com.example.retailease.models.Salesman
import com.example.retailease.models.WholesaleProduct
import kotlinx.serialization.Serializable

@Serializable
data class RetailEaseBackup(
    val products : List<Product>,
    val wholesaleProducts: List<WholesaleProduct>,
    val salesmen: List<Salesman>,
)
