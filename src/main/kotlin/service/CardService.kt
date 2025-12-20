package com.example.service

import com.example.db.Cards
import com.example.repository.ICardRepository
import org.koin.java.KoinJavaComponent.inject

class CardService : ICardService {
    private val cardRepository by inject<ICardRepository>(ICardRepository::class.java)
    override fun getCardList(): List<Cards> {
        return cardRepository.getCardList()
    }

    override suspend fun registerCard(
        name: String,
        number: String,
        cardType: String,
        packCode: String,
        rarity: String,
        imageUrl: String,
        regulationMarkCode: String,
        uid: String
    ): Boolean {
        return cardRepository.registerCard(
            name,
            number,
            cardType,
            packCode,
            rarity,
            imageUrl,
            regulationMarkCode,
            uid
        )
    }
}
