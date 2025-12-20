package com.example.module.request

import kotlinx.serialization.Serializable

@Serializable
data class CardRequest(
    val name: String?,
    val number: String?,
    val cardType: String?,
    val packCode: String?,
    val rarity: String?,
    val regulationMarkCode: String?,
)