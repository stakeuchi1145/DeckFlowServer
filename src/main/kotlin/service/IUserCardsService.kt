package com.example.service

import com.example.db.UserCard

interface IUserCardsService {
    fun getUserCardByUid(uid: String): List<UserCard>
}