package com.example.retailease.ui.uiModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingItem(
    val title: String,
    val description: String = "",
    val icon: ImageVector
)
