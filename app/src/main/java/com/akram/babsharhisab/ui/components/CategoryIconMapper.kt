package com.akram.babsharhisab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconMapper {
    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            "PointOfSale" -> Icons.Default.PointOfSale
            "Payments" -> Icons.Default.Payments
            "Inventory2", "ShoppingBag" -> Icons.Default.Inventory2
            "LocalGasStation" -> Icons.Default.LocalGasStation
            "LocalShipping" -> Icons.Default.LocalShipping
            "Restaurant" -> Icons.Default.Restaurant
            "CarRepair", "Build" -> Icons.Default.Build
            "BatteryChargingFull" -> Icons.Default.BatteryChargingFull
            "Home" -> Icons.Default.Home
            else -> Icons.AutoMirrored.Filled.ReceiptLong
        }
    }
}
