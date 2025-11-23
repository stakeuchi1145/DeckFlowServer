package com.example

import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.startKoin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    startKoin { modules(KoinModule.appModule()) }

    configureRouting()
}
