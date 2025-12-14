package com.example

import com.example.repository.CardRepository
import com.example.repository.ICardRepository
import com.example.repository.IPackRepository
import com.example.repository.IUserCardsRepository
import com.example.service.FirebaseService
import com.example.repository.IUserRepository
import com.example.repository.PackRepository
import com.example.repository.UserCardsRepository
import com.example.repository.UserRepository
import com.example.service.CardService
import com.example.service.ICardService
import com.example.service.IPackService
import com.example.service.IUserCardsService
import com.example.service.IUserService
import com.example.service.PackService
import com.example.service.UserCardsService
import com.example.service.UserService
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
        single<IUserCardsRepository> { UserCardsRepository() }
        single<ICardRepository> { CardRepository() }
        single<IPackRepository> { PackRepository() }

        single<FirebaseService> {
            val credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH") ?: "serviceAccountKey.json"
            val serviceAccount =
                FileInputStream(credentialsPath)

            val options: FirebaseOptions? = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)

            FirebaseService()
        }

        single<IUserService> { UserService() }
        single<IUserCardsService> { UserCardsService() }
        single<ICardService> { CardService() }
        single<IPackService> { PackService() }
    }
}
