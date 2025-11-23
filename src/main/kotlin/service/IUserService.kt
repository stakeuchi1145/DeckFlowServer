package com.example.service

import com.example.db.Users
import com.example.repository.IUserRepository
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

interface IUserService {
    fun getUser(uid: String): Users?
}
