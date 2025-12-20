package com.example.module.response

import kotlinx.serialization.Serializable

@Serializable
data class GetPackListResponse(
    val packs: List<Pack>
)
@Serializable
data class Pack(
    val id: Int,
    val name: String,
    val code: String,
    val totalCards: Int,
    val releaseDate: String,
    val imageUrl: String
)