package io.viamo.flow.runner.flowspec.block.type.message

import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.BlockUIMetadata
import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock.Companion.MESSAGE_TYPE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

interface IMessageBlock : IBlock {
  override val config: IMessageBlockConfig
}

@Serializable
@SerialName(MESSAGE_TYPE)
data class MessageBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: BlockUIMetadata,
  override val exits: List<BlockExit>,
  override val config: MessageBlockConfig
) : IMessageBlock {
  companion object {
    const val MESSAGE_TYPE = "MobilePrimitives.Message"
  }
}
