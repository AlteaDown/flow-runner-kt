package io.viamo.flow.runner

import kotlinx.css.*
import styled.StyleSheet

object WelcomeStyles : StyleSheet("io.viamo.flow.runner.WelcomeStyles", isStatic = true) {
  val textContainer by css {
    padding(5.px)

    backgroundColor = rgb(8, 97, 22)
    color = rgb(56, 246, 137)
  }

  val textInput by css {
    margin(vertical = 5.px)

    fontSize = 14.px
  }
}
