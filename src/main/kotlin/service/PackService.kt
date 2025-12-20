package com.example.service

import com.example.db.Packs
import com.example.repository.IPackRepository
import org.koin.java.KoinJavaComponent.inject

class PackService : IPackService {
    private val packRepository by inject<IPackRepository>(IPackRepository::class.java)

    override fun getPackList(): List<Packs> {
        return packRepository.getPackList()
    }

    override suspend fun registerPack(name: String, code: String, totalCards: Int, releaseDate: String, imageUrl: String): Boolean {
        return packRepository.registerPack(name, code, totalCards, releaseDate, imageUrl)
    }
}
