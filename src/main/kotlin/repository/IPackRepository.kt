package com.example.repository

import com.example.db.Packs

interface IPackRepository {
    fun getPackList(): List<Packs>
    suspend fun registerPack(name: String, code: String, totalCards: Int, releaseDate: String, imageUrl: String): Boolean
}
