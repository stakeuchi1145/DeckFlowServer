package com.example.service

import com.example.db.Users

interface IUserService {
    fun getUser(uid: String): Users?
}
