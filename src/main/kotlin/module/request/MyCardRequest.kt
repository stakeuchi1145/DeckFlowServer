package com.example.module.request

data class MyCardRequest(
    val cardName: String?,
    val code: String?,
    val packName: String?,
    val quantity: Int?,
    val location: String?
)
