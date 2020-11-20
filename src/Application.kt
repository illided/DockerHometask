package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    embeddedServer(Netty, 8080) {
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respondText(cause.message ?: "Something went wrong")
            }
        }

        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }

        install(Routing) {
            route("") {
                get("/whoAmI") {
                    val nickname =
                        call.parameters["githubNickname"] ?: throw IllegalArgumentException("No nickname provided")
                    val totalContributions = Connector.getTotalContributions(nickname)
                    val monster = Connector.getMonster(totalContributions)

                    call.respond(Result(nickname, totalContributions, monster))
                }
            }
        }
    }.start(wait = true)
}
