package io.viamo.flow.runner.flowspec.block.type.message

import PromptValidationException
import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.flowspec.Context
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts

const val MESSAGE_PROMPT_KEY = "Message"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to present a message to an {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
@Serializable
data class MessagePrompt(
  override val context: Context,
  override val config: MessagePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<String?>() {

  override val key = MESSAGE_PROMPT_KEY

  override fun validate(value: Any?) = true
}

@ExperimentalContracts
suspend fun BasePrompt<*>?.expectMessagePrompt(promptAction: suspend MessagePrompt.() -> Cursor?): Cursor? {
  checkNotNull(this)
  check(this is MessagePrompt)
  return this.promptAction()
}

@ExperimentalContracts
suspend fun MessagePrompt.submit() = fulfill(value = "")