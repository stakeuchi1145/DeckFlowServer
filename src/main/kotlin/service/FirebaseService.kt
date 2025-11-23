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
}
