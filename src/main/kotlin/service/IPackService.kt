package com.example.service

import com.example.db.Packs

interface IPackService {
    fun getPackList(): List<Packs>
    suspend fun registerPack(name: String, code: String, totalCards: Int, releaseDate: String, imageUrl: String): Boolean
}
