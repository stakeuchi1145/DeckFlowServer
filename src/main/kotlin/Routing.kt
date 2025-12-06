package com.example

import com.example.module.response.GetMyCardListResponse
import com.example.module.response.GetUserResponse
import com.example.module.response.MyCard
import com.example.service.IUserCardsService
import com.example.service.IUserService
import com.example.util.format
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    val userService: IUserService by inject(IUserService::class.java)
    val userCardsService: IUserCardsService by inject(IUserCardsService::class.java)

    routing {
        get("/") {
            call.respondText("Hello World!")
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

            get("me/cards") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let {
                    if (uid.isNotEmpty()) {
                        val response = GetMyCardListResponse(mutableListOf())
                        val cards = userCardsService.getUserCardByUid(it)
                        cards.forEach { card ->
                            val myCard = MyCard(
                                id = card.id,
                                cardName = card.cardName,
                                imageURL = card.imageURL,
                                packName = card.packName,
                                quantity = card.quantity
                            )

                            response.myCards.add(myCard)
                        }

                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
                    }
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }
        }
    }
}
