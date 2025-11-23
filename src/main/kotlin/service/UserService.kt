package com.example.service

import com.example.db.Users
import com.example.repository.IUserRepository
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class UserService: IUserService {
    private val userRepository: IUserRepository by inject(IUserRepository::class.java)

    override fun getUser(uid: String): Users? {
        return userRepository.getUser(uid)
    }
}
