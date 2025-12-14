package com.example.repository

import com.example.db.Cards

interface ICardRepository {
    fun getCardList(): List<Cards>
}