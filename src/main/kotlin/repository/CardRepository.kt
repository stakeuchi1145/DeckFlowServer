package com.example.repository

import com.example.db.Cards
import com.example.db.SqlLoader
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource

class CardRepository : ICardRepository {
    private val dataSource by inject<DataSource>(DataSource::class.java)

    override fun getCardList(): List<Cards> {
        val sql = SqlLoader.load("cards/select_cards.sql")
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).executeQuery().use { result ->
                return mutableListOf<Cards>().apply {
                    while (result.next()) {
                        add(
                            Cards(
                                id = result.getInt("card_id"),
                                name = result.getString("card_name"),
                                number = result.getString("card_number"),
                                cardType = result.getString("card_type") ?: "",
                                packName = result.getString("pack_name"),
                                rarity = result.getString("rarity"),
                                imageUrl = result.getString("image_url"),
                                regulationMark = result.getString("regulation_mark")
                            )
                        )
                    }
                }
            }
        }
    }
}