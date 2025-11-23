package com.example.repository

import com.example.db.Users

interface IUserRepository {
    fun getUser(uid: String): Users?

    fun getUserByEmail(email: String): Users?
}