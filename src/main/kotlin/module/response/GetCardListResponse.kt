package com.example.module.response

import kotlinx.serialization.Serializable

@Serializable
data class GetCardListResponse(
    val cards: List<Card>
)

@Serializable
data class Card(
    val id: Int,
    val name: String,
    val number: String,
    val cardType: String,
    val packName: String,
    val rarity: String,
    val imageUrl: String,
    val regulationMark: String
)