package io.viamo.flow.runner.block.type.message

import io.viamo.flow.runner.block.Block
import io.viamo.flow.runner.block.BlockExit
import io.viamo.flow.runner.block.BlockUIMetadata
import io.viamo.flow.runner.block.IBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

interface IMessageBlock : IBlock {
  override val config: IMessageBlockConfig
}

abstract class AbstractMessageBlock : Block() {
  abstract override val config: IMessageBlockConfig
}

@Serializable
@SerialName("Message")
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
) : IMessageBlock
