package com.example

import com.example.repository.IUserRepository
import com.example.repository.UserRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.NonCancellable.get
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource

fun Application.configureRouting() {
    startKoin { modules(KoinModule.appModule()) }

    val userRepository: IUserRepository = UserRepository()

    routing {
        get("/") {
            val user = userRepository.getUser()
            println( "user: $user")
            call.respondText("Hello World!")
        }
    }
}
