package io.viamo.flow.runner


fun main() {
  window.onload = {
    render(document.getElementById("root")) {
      child(Welcome::class) {
        attrs {
          name = "Kotlin/JS"
        }
      }
    }
  }
}
