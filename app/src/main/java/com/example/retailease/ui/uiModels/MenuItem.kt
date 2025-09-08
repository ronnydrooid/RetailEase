package com.example.retailease.ui.uiModels

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: Int,
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)
