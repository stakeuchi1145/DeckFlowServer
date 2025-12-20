package com.example.repository

import com.example.db.Packs
import com.example.db.SqlLoader
import com.example.util.toDate
import com.example.util.tx
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource

class PackRepository : IPackRepository {
    private val dataSource by inject<DataSource>(DataSource::class.java)

    override fun getPackList(): List<Packs> {
        dataSource.connection.use { conn ->
            conn.prepareStatement("SELECT * FROM packs").executeQuery().use { result ->
                return mutableListOf<Packs>().apply {
                    while (result.next()) {
                        add(
                            Packs(
                                id = result.getInt("id"),
                                name = result.getString("name"),
                                code = result.getString("code"),
                                totalCards = result.getInt("total_cards"),
                                releaseDate = result.getString("release_date"),
                                imageUrl = result.getString("image_url")
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun registerPack(
        name: String,
        code: String,
        totalCards: Int,
        releaseDate: String,
        imageUrl: String
    ): Boolean {
        try {
            val sql = SqlLoader.load("packs/insert_pack.sql")
            return dataSource.tx {
                val date = java.sql.Date(releaseDate.toDate().time)
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setString(1, name)
                        stmt.setString(2, code)
                        stmt.setInt(3, totalCards)
                        stmt.setDate(4, date)
                        stmt.setString(5, imageUrl)
                        stmt.executeQuery().use {result ->
                            return@tx result.next()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }
}
