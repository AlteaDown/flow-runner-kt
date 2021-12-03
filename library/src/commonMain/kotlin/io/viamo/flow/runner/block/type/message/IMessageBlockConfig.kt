package io.viamo.flow.runner.block.type.message

import io.viamo.flow.runner.block.IBlockConfig
import kotlinx.serialization.Serializable

interface IMessageBlockConfig : IBlockConfig {
  val prompt: String
}

@Serializable
data class MessageBlockConfig(
  override val prompt: String
) : IMessageBlockConfig