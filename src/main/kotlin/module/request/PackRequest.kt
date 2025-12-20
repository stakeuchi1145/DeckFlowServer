package com.example.module.request

import kotlinx.serialization.Serializable

@Serializable
data class PackRequest(
    val name: String?,
    val code: String?,
    val totalCards: Int,
    val releaseDate: String?,
    val fileName: String?
)
