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
import com.example.service.IS3Service
import com.example.service.IUserCardsService
import com.example.service.IUserService
import com.example.service.PackService
import com.example.service.S3Service
import com.example.service.UserCardsService
import com.example.service.UserService
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.io.FileInputStream
import java.net.URI
import javax.sql.DataSource

object KoinModule {
    fun appModule() = module {
        single<DataSource> {
            // --- DB設定 ---
            val config = HikariConfig().apply {
                jdbcUrl = System.getenv("DB_URL") ?: throw IllegalStateException("DB_URL is not set")
                username = System.getenv("DB_USER") ?: throw IllegalStateException("DB_USER is not set")
                password = System.getenv("DB_PASSWORD") ?: throw IllegalStateException("DB_PASSWORD is not set")
                maximumPoolSize = 10
            }

            HikariDataSource(config)
        }

        single<IUserRepository> { UserRepository() }
        single<IUserCardsRepository> { UserCardsRepository() }
        single<ICardRepository> { CardRepository() }
        single<IPackRepository> { PackRepository() }

        single<FirebaseService> {
            val credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH") ?: throw IllegalStateException("FIREBASE_CREDENTIALS_PATH is not set")
            val serviceAccount =
                FileInputStream(credentialsPath)

            val options: FirebaseOptions? = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)

            FirebaseService()
        }

        single<S3Client> {
            S3Client.builder()
                .endpointOverride(
                    System.getenv("S3_ENDPOINT")?.let {
                        URI.create(it) ?: throw IllegalStateException("S3_ENDPOINT is not valid")
                    }
                )
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            System.getenv("S3_ACCESS_KEY_ID") ?: throw IllegalStateException("S3_ACCESS_KEY_ID is not set"),
                            System.getenv("S3_SECRET_ACCESS_KEY") ?: throw IllegalStateException("S3_SECRET_ACCESS_KEY is not set")
                        )
                    )
                )
                .region(Region.AP_NORTHEAST_1)
                .forcePathStyle(true)
                .build()
        }

        single<IUserService> { UserService() }
        single<IUserCardsService> { UserCardsService() }
        single<ICardService> { CardService() }
        single<IPackService> { PackService() }
        single<IS3Service> { S3Service() }
    }
}
