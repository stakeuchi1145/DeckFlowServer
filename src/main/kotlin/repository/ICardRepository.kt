package com.example.repository

import com.example.db.Cards

interface ICardRepository {
    fun getCardList(): List<Cards>
    suspend fun registerCard(name: String, number: String, cardType: String, packCode: String, rarity: String, imageUrl: String, regulationMarkCode: String, uid: String): Boolean
}