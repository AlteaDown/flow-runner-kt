package io.viamo.flow.runner


external interface WelcomeProps : RProps {
  var name: String
}

data class WelcomeState(val name: String) : RState

@JsExport
class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {

  init {
    state = WelcomeState(props.name)
  }

  override fun RBuilder.render() {
    styledDiv {
      css {
        +WelcomeStyles.textContainer
      }
      +"Hello, ${state.name}"
    }
    styledInput {
      css {
        +WelcomeStyles.textInput
      }
      attrs {
        type = InputType.text
        value = state.name
        onChangeFunction = { event ->
          setState(
            WelcomeState(name = (event.target as HTMLInputElement).value)
          )
        }
      }
    }
  }
}
