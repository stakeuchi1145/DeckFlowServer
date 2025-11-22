package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import javax.sql.DataSource

object KoinModule {
    fun appModule() = module {
        single<DataSource> {
            // --- DB設定 ---
            val config = HikariConfig().apply {
                jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:15432/app"
                username = System.getenv("DATABASE_USER") ?: "app"
                password = System.getenv("DATABASE_PASSWORD") ?: "secret"
                maximumPoolSize = 10
            }

            HikariDataSource(config)
        }
    }
}
