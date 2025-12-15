package com.example.repository

import com.example.db.UserCard

interface IUserCardsRepository {
    fun getUserCardByUid(uid: String): List<UserCard>
    suspend fun registerUserCard(email: String, cardName: String, code: String, number: String, quantity: Int, location: String): Boolean
}
