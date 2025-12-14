package com.example.repository

import com.example.db.Packs
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
}