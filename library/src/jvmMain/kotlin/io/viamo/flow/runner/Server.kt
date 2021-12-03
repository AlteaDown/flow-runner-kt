package io.viamo.flow.runner

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
  embeddedServer(Netty, port = 8082, host = "localhost") {
    routing {
      get("/") {
        call.respondText("")

        //call.respondHtml(HttpStatusCode.OK, HTML::index)
      }
      static("/static") {
        resources()
      }
    }
  }.start(wait = true)
}

