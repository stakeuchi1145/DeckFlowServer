package com.example

import com.example.repository.FirebaseService
import com.example.repository.IUserRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    val userRepository: IUserRepository by inject(IUserRepository::class.java)
    val firebaseService: FirebaseService by inject(FirebaseService::class.java)

    routing {
        get("/") {
            val user = userRepository.getUser()
            println( "user: $user")
            call.respondText("Hello World!")
        }

        get("me") {
            val userName = firebaseService.getUid()
            call.respondText("Hello $userName!")
        }
    }
}
