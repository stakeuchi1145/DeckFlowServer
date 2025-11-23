package com.example

import com.example.repository.FirebaseService
import com.example.repository.IUserRepository
import com.example.repository.UserRepository
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import java.io.FileInputStream
import javax.sql.DataSource

object KoinModule {
    fun appModule() = module {
        single<DataSource> {
            // --- DB設定 ---
            val config = HikariConfig().apply {
                jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:15432/deckflow"
                username = System.getenv("DB_USER") ?: "deckflow_user"
                password = System.getenv("DB_PASSWORD") ?: "secret"
                maximumPoolSize = 10
            }

            HikariDataSource(config)
        }

        single<IUserRepository> { UserRepository() }

        single<FirebaseService> {
            val serviceAccount =
                FileInputStream("serviceAccountKey.json")

            val options: FirebaseOptions? = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)

            FirebaseService()
        }
    }
}
