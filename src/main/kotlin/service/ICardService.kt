package com.example.service

import com.example.db.Cards

interface ICardService {
    fun getCardList(): List<Cards>
}
