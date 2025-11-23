package com.example.repository

import com.google.firebase.auth.FirebaseAuth

class FirebaseService {
    fun getUid(): String {
        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken("")
            return decodedToken.uid
        } catch (e: Exception) {
            println(e.message)
        }

        return ""
    }
}
