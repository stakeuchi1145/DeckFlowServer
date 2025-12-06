package com.example.db

object SqlLoader {
    fun load(path: String): String {
        val url = SqlLoader::class.java.getResource("/sql/$path")
            ?: error("SQL not found: /sql/$path")
        return url.readText()
    }
}
