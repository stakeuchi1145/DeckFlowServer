package com.example.repository

import com.example.db.SqlLoader
import com.example.db.UserCard
import com.example.util.tx
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

    override suspend fun registerUserCard(email: String) {
        dataSource.tx { conn ->
            conn.autoCommit = false

            val sql = SqlLoader.load("user_cards/insert_user_card.sql")
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.setString(2, userCard.imageURL)
                stmt.setString(3, userCard.packName)
                stmt.setInt(4, userCard.quantity)
                stmt.setString()
                stmt.setString()
                stmt.executeUpdate()
            }
        }
    }
}