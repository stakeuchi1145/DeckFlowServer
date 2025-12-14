package com.example.service

import com.example.db.Packs

interface IPackService {
    fun getPackList(): List<Packs>
}
