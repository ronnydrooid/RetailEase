package com.example.retailease.ui.uiModels

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title: String,
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)