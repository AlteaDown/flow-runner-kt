package io.viamo.flow.runner


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
