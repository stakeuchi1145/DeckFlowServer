package com.example.repository

import com.example.db.Users
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource
import kotlin.getValue

class UserRepository: IUserRepository {
    private val dataSource by inject<DataSource>(DataSource::class.java)

    override fun getUser(): Users? {
        // --- DBアクセス ---
        dataSource.connection.use { conn ->
            println("✅ Connected to PostgreSQL!")

            // SELECT
            val stmt = conn.prepareStatement("SELECT * FROM users")
            val rs = stmt.executeQuery()
            println("📋 users table:")
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("display_name")
                val email = rs.getString("email")
                val authProvider = rs.getString("auth_provider")
                val authId = rs.getString("auth_uid")
                val created = rs.getTimestamp("created_at")
                val updated = rs.getTimestamp("updated_at")

                return Users(
                    id = id,
                    displayName = name,
                    email = email,
                    authProvider = authProvider,
                    authId = authId,
                    createdAt = created,
                    updatedAt = updated
                )
            }
        }

        return null
    }
}
