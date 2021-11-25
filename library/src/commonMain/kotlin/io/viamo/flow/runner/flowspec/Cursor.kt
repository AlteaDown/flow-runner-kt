package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig

data class Cursor(
  override val interactionId: String,
  override val promptConfig: IPromptConfig<*>?,
) : ICursor

interface ICursor {
  /**
   * UUID of the current interaction with a block.
   */
  val interactionId: String

  /**
   * A prompt configuration data object; optional, because not every block requests input from the io.viamo.flow.runner."flow-spec".Contact.
   * If it does, we call it an "io.viamo.flow.runner."flow-spec".ICursorInputRequired".
   * If not, "io.viamo.flow.runner."flow-spec".ICursorNoInputRequired" will have a "null-ish" "promptConfig".
   */
  val promptConfig: IPromptConfig<*>?
}

interface ICursorInputRequired : ICursor {
  override val interactionId: String
  override val promptConfig: IPromptConfig<*>
}

interface ICursorNoInputRequired : ICursor {
  override val interactionId: String
  override val promptConfig: IPromptConfig<*>?
}

interface IRichCursor {
  /**
   * An object representation of the current interaction with a block.
   */
  val interaction: IBlockInteraction

  /**
   * In io.viamo.flow.runner.domain.prompt.IPrompt instance.
   * When present, we call it a TRichCursorInputRequired.
   * In absence, the TRichCursorNoInputRequired will maintain "prompt" with a null-ish value.
   */
  var prompt: BasePrompt<*>?
}

data class RichCursor(
  override val interaction: IBlockInteraction,

  /**
   * In io.viamo.flow.runner.domain.prompt.IPrompt instance.
   * When present, we call it a TRichCursorInputRequired.
   * In absence, the TRichCursorNoInputRequired will maintain "prompt" with a null-ish value.
   */
  override var prompt: BasePrompt<*>?,
) : IRichCursor

interface IRichCursorInputRequired {
  val interaction: IBlockInteraction
  val prompt: BasePrompt<*>
}

data class RichCursorInputRequired(
  override val interaction: IBlockInteraction,
  override val prompt: BasePrompt<*>,
) : IRichCursorInputRequired