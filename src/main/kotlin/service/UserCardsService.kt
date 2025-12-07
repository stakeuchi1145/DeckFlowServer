package com.example.service

import com.example.db.UserCard
import com.example.repository.IUserCardsRepository
import org.koin.java.KoinJavaComponent.inject

class UserCardsService : IUserCardsService {
    val userCardsRepository: IUserCardsRepository by inject(IUserCardsRepository::class.java)

    override fun getUserCardByUid(uid: String): List<UserCard> {
        return userCardsRepository.getUserCardByUid(uid)
    }
}
