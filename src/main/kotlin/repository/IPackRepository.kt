package com.example.repository

import com.example.db.Packs

interface IPackRepository {
    fun getPackList(): List<Packs>
}
