package com.example.module.request

import kotlinx.serialization.Serializable

@Serializable
data class MyCardRequest(
    val cardName: String?,
    val packCode: String?,
    val cardNumber: String?,
    val quantity: Int?,
    val location: String?
)
