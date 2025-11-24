package com.example.service

import com.google.firebase.auth.FirebaseAuth


class FirebaseService {
    fun getUid(token: String): String {
        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
            return decodedToken.uid
        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }

    fun createCustomToken(uid: String): String {
        val additionalClaims: MutableMap<String?, Any?> = HashMap()
        additionalClaims["premiumAccount"] = true

        return FirebaseAuth.getInstance().createCustomToken(uid, additionalClaims)
    }
}
