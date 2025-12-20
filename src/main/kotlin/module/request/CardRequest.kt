package com.example.module.request

data class CardRequest(
    val name: String,
    val number: String,
    val cardType: String,
    var packName: String,
    val rarity: String,
    val regulationMark: String,
)