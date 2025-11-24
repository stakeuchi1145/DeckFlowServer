package com.example.module.response

import kotlinx.serialization.*

@Serializable
data class GetUserResponse(
    val displayName: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
)
