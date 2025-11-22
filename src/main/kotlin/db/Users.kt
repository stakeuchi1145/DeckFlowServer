package com.example.db

import java.util.Date

data class Users(
    val id: String,
    val displayName: String,
    val email: String,
    val authProvider: String,
    val authId: String,
    val createdAt: Date,
    val updatedAt: Date
)
