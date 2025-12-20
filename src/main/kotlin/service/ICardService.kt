package com.example.service

import com.example.db.Cards

interface ICardService {
    fun getCardList(): List<Cards>
    suspend fun registerCard(name: String, number: String, cardType: String, packCode: String, rarity: String, imageUrl: String, regulationMarkCode: String, uid: String): Boolean
}
