package io.viamo.flow.runner.domain

import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.BlockInteraction
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

sealed class Cursor {

  abstract fun findInteraction(context: Context): BlockInteraction
  abstract fun findInteractionUuid(): String
  abstract fun findPromptConfig(): IPromptConfig<out @Contextual Any?>?
  abstract fun findPrompt(flowRunner: FlowRunner, context: Context): BasePrompt<out @Contextual Any?>?

  @Serializable
  data class BasicCursor(
    override val interactionId: String,
    override val promptConfig: IPromptConfig<out @Contextual Any?>?,
  ) : Cursor(), ICursor {

    override fun findInteractionUuid() = interactionId
    override fun findInteraction(context: Context) = context.findInteractionWith(interactionId)
    override fun findPromptConfig() = promptConfig
    override fun findPrompt(flowRunner: FlowRunner, context: Context) =
        flowRunner.createPromptFrom(context, promptConfig, findInteraction(context))

    fun toRich(flowRunner: FlowRunner, context: Context): RichCursor {
      val interaction = context.findInteractionWith(interactionId)
      return RichCursor(
        interaction = interaction,
        prompt = findPrompt(flowRunner, context)
      )
    }
  }

  @Serializable
  class RichCursor(
    override val interaction: BlockInteraction,
    @Contextual
    override val prompt: BasePrompt<out @Contextual Any?>?,
  ) : Cursor(), IRichCursor {

    override fun findInteractionUuid() = interaction.uuid
    override fun findInteraction(context: Context) = interaction
    override fun findPromptConfig() = prompt?.config
    override fun findPrompt(flowRunner: FlowRunner, context: Context) = prompt

    fun toBasic() = BasicCursor(interaction.uuid, prompt?.config)
  }
}

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
  val prompt: BasePrompt<*>?
}

object CursorSerializer : KSerializer<Cursor> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Cursor")

  override fun serialize(encoder: Encoder, value: Cursor) {
    encoder.encodeString(value.findInteractionUuid())
  }

  override fun deserialize(decoder: Decoder): Cursor {
    return Cursor.BasicCursor(
      interactionId = decoder.decodeString(),
      promptConfig = null
    )
  }
}