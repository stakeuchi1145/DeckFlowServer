package com.example.module.response

import kotlinx.serialization.Serializable

@Serializable
data class GetMyCardListResponse(
    val myCards: MutableList<MyCard>
)

@Serializable
data class MyCard(
    val id: Int,
    val cardName: String,
    val imageURL: String,
    val packName: String,
    val quantity: Int
)
