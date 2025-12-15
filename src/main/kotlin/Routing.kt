package com.example

import com.example.module.request.MyCardRequest
import com.example.module.response.Card
import com.example.module.response.GetCardListResponse
import com.example.module.response.GetMyCardListResponse
import com.example.module.response.GetPackListResponse
import com.example.module.response.GetUserResponse
import com.example.module.response.MyCard
import com.example.module.response.Pack
import com.example.service.ICardService
import com.example.service.IPackService
import com.example.service.IUserCardsService
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    val userService: IUserService by inject(IUserService::class.java)
    val userCardsService: IUserCardsService by inject(IUserCardsService::class.java)
    val cardService: ICardService by inject(ICardService::class.java)
    val packService: IPackService by inject(IPackService::class.java)

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

            post("me/card") {
                val uid = call.principal<UserIdPrincipal>()?.name
                uid?.let {
                    val request = call.receive<MyCardRequest>()
                    val cardName = request.cardName ?: return@post call.respond(HttpStatusCode.BadRequest, "cardId is missing")
                    val code = request.code ?: return@post call.respond(HttpStatusCode.BadRequest, "code is missing")
                    val packName = request.packName ?: return@post call.respond(HttpStatusCode.BadRequest, "packName is missing")
                    val quantity = request.quantity ?: return@post call.respond(HttpStatusCode.BadRequest, "quantity is missing")
                    val location = request.location ?: return@post call.respond(HttpStatusCode.BadRequest, "location is missing")
                    val email: String = userService.getUser(uid)?.email ?: return@post call.respond(HttpStatusCode.BadRequest, "email is missing")

                    CoroutineScope(Dispatchers.Main).launch {
                        if (email.isNotEmpty() && cardName.isNotEmpty() && code.isNotEmpty() && packName.isNotEmpty()) {
                            val result = userCardsService.registerUserCard(email, cardName, code, packName, quantity, location)
                            if (result) {
                                call.respond(HttpStatusCode.OK, "Card registered successfully")
                            } else {
                                call.respond(HttpStatusCode.InternalServerError, "Failed to register card")
                            }
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "Invalid data")
                        }
                    }
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }

            get("cards") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let {
                    val cards = mutableListOf<Card>().apply {
                        cardService.getCardList().forEach { card ->
                            add(
                                Card(
                                    id = card.id,
                                    name = card.name,
                                    number = card.number,
                                    cardType = card.cardType,
                                    packName = card.packName,
                                    rarity = card.rarity,
                                    imageUrl = card.imageUrl,
                                    regulationMark = card.regulationMark
                                )
                            )
                        }
                    }

                    call.respond(HttpStatusCode.OK, GetCardListResponse(cards))
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }

            post("cards") {
                val uid = call.principal<UserIdPrincipal>()?.name
                uid?.let {
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }
            get("packs") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let {
                    val packs = mutableListOf<Pack>().apply {
                        packService.getPackList().forEach { pack ->
                            add(
                                Pack(
                                    id = pack.id,
                                    name = pack.name,
                                    code = pack.code,
                                    totalCards = pack.totalCards,
                                    releaseDate = pack.releaseDate,
                                    imageUrl = pack.imageUrl
                                )
                            )
                        }
                    }

                    call.respond(
                        HttpStatusCode.OK,
                        GetPackListResponse(packs)
                    )
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }
        }
    }
}
