package com.example.util

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val COST = 12 // 10〜14くらいが現実的

    fun hash(rawPassword: String): String {
        return BCrypt.withDefaults()
            .hashToString(COST, rawPassword.toCharArray())
    }

    fun verify(rawPassword: String, hashedPassword: String): Boolean {
        val result = BCrypt.verifyer()
            .verify(rawPassword.toCharArray(), hashedPassword.toCharArray())
        return result.verified
    }
}