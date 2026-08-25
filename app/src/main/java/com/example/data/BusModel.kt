package com.example.data

import androidx.compose.ui.graphics.Color

data class BusModel(
    val id: String,
    val name: String,
    val category: String, // e.g. "HDD", "Double Decker", "SHD", "Truck Mod"
    val baseSpeedKmH: Int,
    val chassisPowerHp: Int,
    val priceRp: Long,
    val isUnlocked: Boolean = false,
    val primaryColor: Color,
    val secondaryColor: Color,
    val description: String,
    val liveryName: String
)

object DefaultBusses {
    val list = listOf(
        BusModel(
            id = "bus_jb5",
            name = "Jetbus 5 HDD Ultra",
            category = "High Deck",
            baseSpeedKmH = 180,
            chassisPowerHp = 450,
            priceRp = 1_500_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFFE11D48),
            secondaryColor = Color(0xFF0F172A),
            description = "Generasi terbaru Jetbus 5 dengan suspensi udara & lampu LED modern.",
            liveryName = "Sinar Jaya Premium Red"
        ),
        BusModel(
            id = "bus_sr3_dd",
            name = "SR3 Double Decker Luxury",
            category = "Double Decker",
            baseSpeedKmH = 210,
            chassisPowerHp = 520,
            priceRp = 3_200_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFF0284C7),
            secondaryColor = Color(0xFFF8FAFC),
            description = "Bus 2 tingkat super mewah kelas Sleeper Seat & Executive Class.",
            liveryName = "Rosalia Indah Super Top"
        ),
        BusModel(
            id = "bus_avante_h9",
            name = "Avante H9 Grand Panorama",
            category = "Super High Deck",
            baseSpeedKmH = 195,
            chassisPowerHp = 480,
            priceRp = 2_100_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFF16A34A),
            secondaryColor = Color(0xFFFEF08A),
            description = "Desain kaca depan jenong panorama luas untuk pemandangan maksimal.",
            liveryName = "Harapan Jaya Metallic Green"
        ),
        BusModel(
            id = "bus_canter_mod",
            name = "Truck Canter Oleng Mod",
            category = "Truck Mod",
            baseSpeedKmH = 240,
            chassisPowerHp = 600,
            priceRp = 850_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFFCA8A04),
            secondaryColor = Color(0xFF18181B),
            description = "Mod truk canter terlaris dengan variasi terpal & sirine oleng.",
            liveryName = "Wahyu Abadi Yellow Custom"
        ),
        BusModel(
            id = "bus_tourismo",
            name = "Tourismo Mercedes O500R",
            category = "European Class",
            baseSpeedKmH = 220,
            chassisPowerHp = 500,
            priceRp = 2_800_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFF9333EA),
            secondaryColor = Color(0xFF3B82F6),
            description = "Sasis Eropa kelas sultan dengan pengereman ABS & retatder kencang.",
            liveryName = "EFISIENSI Purple Jet"
        ),
        BusModel(
            id = "bus_sr2_xhd",
            name = "Legacy SR2 XHD Prime",
            category = "Extra High Deck",
            baseSpeedKmH = 190,
            chassisPowerHp = 460,
            priceRp = 1_800_000_000,
            isUnlocked = true,
            primaryColor = Color(0xFFEA580C),
            secondaryColor = Color(0xFF1E293B),
            description = "Desain garang dengan bodi Laksana ikonik Lintas Sumatra.",
            liveryName = "PO Haryanto Menara Kudus"
        )
    )
}
