package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "name_bn")
    val nameBn: String,

    @ColumnInfo(name = "type")
    val type: String, // SALE, BAKI_COLLECT, PURCHASE, EXPENSE, ASSET

    @ColumnInfo(name = "icon_name")
    val iconName: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "is_system")
    val isSystem: Boolean = true
) {
    companion object {
        const val CAT_SALE = "CAT_SALE"
        const val CAT_BAKI_COLLECT = "CAT_BAKI_COLLECT"
        const val CAT_PURCHASE = "CAT_PURCHASE"
        const val CAT_FUEL = "CAT_FUEL"
        const val CAT_TRANSPORT = "CAT_TRANSPORT"
        const val CAT_FOOD = "CAT_FOOD"
        const val CAT_REPAIR = "CAT_REPAIR"
        const val CAT_ASSET = "CAT_ASSET"
        const val CAT_OTHER = "CAT_OTHER"
        const val CAT_VENDOR_BAKI_PAY = "CAT_VENDOR_BAKI_PAY"
        const val CAT_HOME_EXPENSE = "CAT_HOME_EXPENSE"

        fun defaultSystemCategories(): List<CategoryEntity> = listOf(
            CategoryEntity(
                categoryId = CAT_SALE,
                nameBn = "পণ্য বিক্রি (নগদ ও বাকি)",
                type = "SALE",
                iconName = "PointOfSale",
                colorHex = "#059669",
                sortOrder = 1,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_BAKI_COLLECT,
                nameBn = "বাকি আদায় (দোকানদার জমা)",
                type = "BAKI_COLLECT",
                iconName = "Payments",
                colorHex = "#D97706",
                sortOrder = 2,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_PURCHASE,
                nameBn = "মাল কেনা (পাইকারি ক্রয়)",
                type = "PURCHASE",
                iconName = "Inventory2",
                colorHex = "#2563EB",
                sortOrder = 3,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_FUEL,
                nameBn = "গাড়ির জ্বালানি / তেল / চার্জ",
                type = "EXPENSE",
                iconName = "LocalGasStation",
                colorHex = "#EA580C",
                sortOrder = 4,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_TRANSPORT,
                nameBn = "যাতায়াত, ভাড়া ও টোল",
                type = "EXPENSE",
                iconName = "LocalShipping",
                colorHex = "#7C3AED",
                sortOrder = 5,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_FOOD,
                nameBn = "খাবার ও নাস্তা ভাতা",
                type = "EXPENSE",
                iconName = "Restaurant",
                colorHex = "#0D9488",
                sortOrder = 6,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_REPAIR,
                nameBn = "গাড়ি মেরামত ও ব্রেকডাউন",
                type = "EXPENSE",
                iconName = "CarRepair",
                colorHex = "#E11D48",
                sortOrder = 7,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_ASSET,
                nameBn = "সম্পদ ও ব্যাটারি রক্ষণাবেক্ষণ",
                type = "ASSET",
                iconName = "BatteryChargingFull",
                colorHex = "#4F46E5",
                sortOrder = 8,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_OTHER,
                nameBn = "অন্যান্য খরচ",
                type = "EXPENSE",
                iconName = "ReceiptLong",
                colorHex = "#64748B",
                sortOrder = 9,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_VENDOR_BAKI_PAY,
                nameBn = "মহাজনের বাকি পরিশোধ",
                type = "VENDOR_BAKI_PAY",
                iconName = "Payments",
                colorHex = "#1D4ED8",
                sortOrder = 10,
                isSystem = true
            ),
            CategoryEntity(
                categoryId = CAT_HOME_EXPENSE,
                nameBn = "বাসা ও সংসারের খরচ",
                type = "PERSONAL",
                iconName = "Home",
                colorHex = "#9333EA",
                sortOrder = 11,
                isSystem = true
            )
        )
    }
}
