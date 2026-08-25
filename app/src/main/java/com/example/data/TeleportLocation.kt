package com.example.data

data class TeleportLocation(
    val id: String,
    val name: String,
    val region: String,
    val coordX: Float,
    val coordY: Float,
    val coordZ: Float,
    val iconName: String,
    val description: String
)

object DefaultLocations {
    val list = listOf(
        TeleportLocation(
            id = "loc_pulo_gebang",
            name = "Terminal Pulo Gebang",
            region = "Jakarta Timur",
            coordX = -6.2145f,
            coordY = 106.9521f,
            coordZ = 18.5f,
            iconName = "location_city",
            description = "Terminal Megah Terbesar Se-Asia Tenggara dengan rute Pantura."
        ),
        TeleportLocation(
            id = "loc_purabaya",
            name = "Terminal Purabaya (Bungurasih)",
            region = "Surabaya",
            coordX = -7.3524f,
            coordY = 112.7212f,
            coordZ = 12.0f,
            iconName = "location_city",
            description = "Pusat keberangkatan bus malam Surabaya - Solo - Jakarta."
        ),
        TeleportLocation(
            id = "loc_kelok_44",
            name = "Kelok 44 Extrema Pass",
            region = "Sumatera Barat",
            coordX = -0.3211f,
            coordY = 100.2189f,
            coordZ = 450.0f,
            iconName = "landscape",
            description = "Jalur ekstrem tikungan patah tebing curam favorit driver profesional."
        ),
        TeleportLocation(
            id = "loc_bandung_leuwi",
            name = "Terminal Leuwi Panjang",
            region = "Bandung",
            coordX = -6.9458f,
            coordY = 107.5954f,
            coordZ = 720.0f,
            iconName = "location_city",
            description = "Gerbang utama Priangan Timur & rute tol Purbaleunyi."
        ),
        TeleportLocation(
            id = "loc_wonosobo_dieng",
            name = "Jalur Pegunungan Dieng",
            region = "Wonosobo",
            coordX = -7.2189f,
            coordY = 109.9021f,
            coordZ = 1850.0f,
            iconName = "terrain",
            description = "Jalur tanjakan ekstrem berembun dingin dengan tikungan S ganda."
        ),
        TeleportLocation(
            id = "loc_bali_denpasar",
            name = "Terminal Ubung Denpasar",
            region = "Bali",
            coordX = -8.6341f,
            coordY = 115.2014f,
            coordZ = 25.0f,
            iconName = "beach_access",
            description = "Penyeberangan kapal Gilimanuk menuju pulau dewata Bali."
        ),
        TeleportLocation(
            id = "loc_lau_kawar",
            name = "Jalur Danau Lau Kawar",
            region = "Medan / Karo",
            coordX = 3.1894f,
            coordY = 98.3841f,
            coordZ = 1200.0f,
            iconName = "alt Route",
            description = "Lintas Sumatra Utara dikelilingi perbukitan hijau kencang."
        )
    )
}
