package com.example.service

import com.example.db.UserCard

interface IUserCardsService {
    fun getUserCardByUid(uid: String): List<UserCard>
    suspend fun registerUserCard(email: String, cardName: String, code: String, number: String, quantity: Int, location: String): Boolean
}
