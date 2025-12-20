package com.example

import com.example.module.request.CardRequest
import com.example.module.request.MyCardRequest
import com.example.module.request.PackRequest
import com.example.module.response.Card
import com.example.module.response.GetCardListResponse
import com.example.module.response.GetMyCardListResponse
import com.example.module.response.GetPackListResponse
import com.example.module.response.GetUserResponse
import com.example.module.response.MyCard
import com.example.module.response.Pack
import com.example.service.ICardService
import com.example.service.IPackService
import com.example.service.IS3Service
import com.example.service.IUserCardsService
import com.example.service.IUserService
import com.example.util.format
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

fun Application.configureRouting() {
    val userService: IUserService by inject(IUserService::class.java)
    val userCardsService: IUserCardsService by inject(IUserCardsService::class.java)
    val cardService: ICardService by inject(ICardService::class.java)
    val packService: IPackService by inject(IPackService::class.java)
    val s3Service: IS3Service by inject(IS3Service::class.java)

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
                    val cardName = request.cardName ?: return@post call.respond(HttpStatusCode.BadRequest, "cardName is missing")
                    val packCode = request.packCode ?: return@post call.respond(HttpStatusCode.BadRequest, "packCode is missing")
                    val cardNumber = request.cardNumber ?: return@post call.respond(HttpStatusCode.BadRequest, "packCode is missing")
                    val quantity = request.quantity ?: return@post call.respond(HttpStatusCode.BadRequest, "quantity is missing")
                    val location = request.location ?: return@post call.respond(HttpStatusCode.BadRequest, "location is missing")
                    val email: String = userService.getUser(uid)?.email ?: return@post call.respond(HttpStatusCode.BadRequest, "email is missing")

                    if (email.isNotEmpty() && cardName.isNotEmpty() && packCode.isNotEmpty() && cardNumber.isNotEmpty()) {
                        val result = userCardsService.registerUserCard(email, cardName, packCode, cardNumber, quantity, location)
                        if (result) {
                            call.respond(HttpStatusCode.OK, "Card registered successfully")
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, "Failed to register card")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid data")
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

            post("card") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let { uid ->
                    val multipart = call.receiveMultipart()
                    var request: CardRequest? = null
                    var fileName = ""
                    var contentType = ""
                    var imageBytes = byteArrayOf()

                    multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                if (part.name == "data") {
                                    request = Json.decodeFromString(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: ""
                                contentType = part.contentType?.toString() ?: ""
                                imageBytes = part.streamProvider().readBytes()
                            }
                            else -> {}
                        }

                        part.dispose()
                    }

                    request?.let {
                        val name = it.name ?: return@post call.respond(HttpStatusCode.BadRequest, "name is missing")
                        val number = it.number ?: return@post call.respond(HttpStatusCode.BadRequest, "number is missing")
                        val cardType = it.cardType ?: return@post call.respond(HttpStatusCode.BadRequest, "cardType is missing")
                        val packCode = it.packCode ?: return@post call.respond(HttpStatusCode.BadRequest, "packName is missing")
                        val rarity = it.rarity ?: return@post call.respond(HttpStatusCode.BadRequest, "rarity is missing")
                        val regulationMarkCode = it.regulationMarkCode ?: return@post call.respond(HttpStatusCode.BadRequest, "regulationMark is missing")

                        if (name.isEmpty() || number.isEmpty() || cardType.isEmpty() || packCode.isEmpty() || rarity.isEmpty() || regulationMarkCode.isEmpty()) {
                            return@post call.respond(HttpStatusCode.BadRequest, "Invalid data")
                        }

                        if (fileName.isNotEmpty() && contentType.isNotEmpty() && imageBytes.isNotEmpty()) {
                            if (!s3Service.uploadImage("card-images", "${packCode}/$fileName", contentType, imageBytes)) {
                                return@post call.respond(HttpStatusCode.InternalServerError, "Failed to upload image")
                            }
                        }

                        val result = cardService.registerCard(
                            name,
                            number,
                            cardType,
                            packCode,
                            rarity,
                            if (fileName.isNotEmpty()) "card-images/${packCode}/$fileName" else "",
                            regulationMarkCode,
                            uid
                        )

                        if (!result && fileName.isNotEmpty()) {
                            s3Service.deleteImage("card-images", "${packCode}/$fileName")
                        }

                        call.respond(HttpStatusCode.OK, mapOf("result" to result))
                    } ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid data")
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

            post("pack") {
                val uid = call.principal<UserIdPrincipal>()?.name

                uid?.let {
                    val multipart = call.receiveMultipart()
                    var request: PackRequest? = null
                    var fileName = ""
                    var contentType = ""
                    var imageBytes = byteArrayOf()

                    multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                if (part.name == "data") {
                                    request = Json.decodeFromString(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: ""
                                contentType = part.contentType?.toString() ?: ""
                                imageBytes = part.streamProvider().readBytes()
                            }
                            else -> {}
                        }

                        part.dispose()
                    }

                    request?.let {
                        val name = it.name ?: return@post call.respond(HttpStatusCode.BadRequest, "name is missing")
                        val code = it.code ?: return@post call.respond(HttpStatusCode.BadRequest, "code is missing")
                        val totalCards = it.totalCards
                        val releaseDate = it.releaseDate ?: return@post call.respond(HttpStatusCode.BadRequest, "releaseDate is missing")

                        if (name.isEmpty() || code.isEmpty() || totalCards <= 0 || releaseDate.isEmpty()) {
                            return@post call.respond(HttpStatusCode.BadRequest, "Invalid data")
                        }

                        if (fileName.isNotEmpty() && contentType.isNotEmpty() && imageBytes.isNotEmpty()) {
                            if (!s3Service.uploadImage("pack-images", "images/$fileName", contentType, imageBytes)) {
                                return@post call.respond(HttpStatusCode.InternalServerError, "Failed to upload image")
                            }
                        }

                        val result = packService.registerPack(
                            name,
                            code,
                            totalCards,
                            releaseDate,
                            if (fileName.isNotEmpty()) "pack-images/images/$fileName" else ""
                        )

                        if (!result && fileName.isNotEmpty()) {
                            s3Service.deleteImage("card-images", "images/$fileName")
                        }

                        call.respond(HttpStatusCode.OK, mapOf("result" to result))
                    } ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid data")
                } ?: call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
            }
        }
    }
}
