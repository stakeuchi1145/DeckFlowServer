package com.example.service

import com.example.db.Cards
import com.example.repository.ICardRepository
import org.koin.java.KoinJavaComponent.inject

class CardService : ICardService {
    private val cardRepository by inject<ICardRepository>(ICardRepository::class.java)
    override fun getCardList(): List<Cards> {
        return cardRepository.getCardList()
    }
}
