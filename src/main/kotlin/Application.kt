package com.example

import com.example.service.FirebaseService
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    startKoin { modules(KoinModule.appModule()) }

    val firebaseService: FirebaseService by inject(FirebaseService::class.java)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(Authentication) {
            bearer("auth-bearer") {
                realm = "Access to the '/' path"
                authenticate { tokenCredential ->
                    val uid = firebaseService.getUid(tokenCredential.token)
                    UserIdPrincipal(uid)
                }
            }
        }

        install(ContentNegotiation) {
            json()
        }

        configureRouting()
    }.start(wait = true)
}

