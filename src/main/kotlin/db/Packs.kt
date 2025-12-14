package com.example.db

import java.util.Date

data class Packs(
    val id: Int,
    val name: String,
    val code: String,
    val totalCards: Int,
    val releaseDate: String,
    val imageUrl: String
    )
