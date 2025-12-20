package com.example.repository

import com.example.db.Cards
import com.example.db.SqlLoader
import com.example.util.tx
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

    override suspend fun registerCard(
        name: String,
        number: String,
        cardType: String,
        packCode: String,
        rarity: String,
        imageUrl: String,
        regulationMarkCode: String,
        uid: String
    ): Boolean {
        try {
            val sql = SqlLoader.load("cards/insert_card.sql")
            return dataSource.tx { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setString(2, number)
                    stmt.setString(3, cardType)
                    stmt.setString(4, packCode)
                    stmt.setString(5, rarity)
                    stmt.setString(6, imageUrl)
                    stmt.setString(7, regulationMarkCode)
                    stmt.setString(8, uid)
                    stmt.executeQuery().use { result ->
                        return@tx result.next()
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }
}