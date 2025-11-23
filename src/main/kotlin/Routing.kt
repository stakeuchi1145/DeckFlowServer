package com.example

import com.example.module.request.LoginRequest
import com.example.module.request.LoginResponse
import com.example.module.response.GetUserResponse
import com.example.service.IUserService
import com.example.util.format
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    val userService: IUserService by inject(IUserService::class.java)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("login") {
            val req = call.receive<LoginRequest>()
            val token = userService.login(req.email, req.password)

            if (token != null) {
                call.respond(LoginResponse(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
            }
        }

        authenticate("auth-bearer") {
            get("me") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let {
                    if (uid.isNotEmpty()) {
                        val user = userService.getUser(it)
                        val response = GetUserResponse(
                            displayName = user?.displayName ?: "",
                            email = user?.email ?: "",
                            createdAt = user?.createdAt?.format() ?: "",
                            updatedAt = user?.updatedAt?.format() ?: "",
                        )

                        call.respond(response)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
                    }
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }
        }
    }
}
