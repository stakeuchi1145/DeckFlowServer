package com.example.repository

import com.example.db.SqlLoader
import com.example.db.UserCard
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource
import kotlin.getValue

class UserCardsRepository : IUserCardsRepository {
    private val dataSource by inject<DataSource>(DataSource::class.java)

    override fun getUserCardByUid(uid: String): List<UserCard> {
        val cards = mutableListOf<UserCard>()
        val sql = SqlLoader.load("user_cards/select_user_card_by_uid.sql")

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, uid)
                stmt.executeQuery().use { result ->
                    while (result.next()) {
                        cards.add(
                            UserCard(
                                id = result.getInt("id"),
                                cardName = result.getString("card_name"),
                                imageURL = result.getString("card_image_url"),
                                packName = result.getString("pack_name"),
                                quantity = result.getInt("quantity")
                            )
                        )
                    }
                }
            }
        }

        return cards
    }
}