package com.example.repository

import com.example.db.Users
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource
import kotlin.getValue

class UserRepository: IUserRepository {
    private val dataSource by inject<DataSource>(DataSource::class.java)

    override fun getUser(uid: String): Users? {
        // --- DBアクセス ---
        dataSource.connection.use { conn ->
            // SELECT
            conn.prepareStatement("SELECT * FROM users WHERE auth_uid = ?").use { stmt ->
                stmt.setString(1, uid)
                stmt.executeQuery().use { result ->
                    if (result.next()) {
                        return Users(
                            id = result.getInt("id"),
                            displayName = result.getString("display_name"),
                            email = result.getString("email"),
                            passwordHash = result.getString("passwordHash"),
                            authProvider = result.getString("auth_provider"),
                            authId = result.getString("auth_uid"),
                            createdAt = result.getTimestamp("created_at"),
                            updatedAt = result.getTimestamp("updated_at")
                        )
                    }
                }
            }
        }

        return null
    }

    override fun getUserByEmail(email: String): Users? {
        // --- DBアクセス ---
        dataSource.connection.use { conn ->
            // SELECT
            conn.prepareStatement("SELECT * FROM users WHERE email = ?").use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { result ->
                    if (result.next()) {
                        return Users(
                            id = result.getInt("id"),
                            displayName = result.getString("display_name"),
                            email = result.getString("email"),
                            passwordHash = result.getString("password_hash"),
                            authProvider = result.getString("auth_provider"),
                            authId = result.getString("auth_uid"),
                            createdAt = result.getTimestamp("created_at"),
                            updatedAt = result.getTimestamp("updated_at")
                        )
                    }
                }
            }
        }

        return null
    }
}
