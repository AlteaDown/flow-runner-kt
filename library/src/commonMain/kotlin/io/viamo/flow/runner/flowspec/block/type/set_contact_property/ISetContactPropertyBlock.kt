package io.viamo.flow.runner.flowspec.block.type.set_contact_property

import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

interface ISetContactPropertyBlock : IBlock {
  override val config: ISetContactPropertyBlockConfig
}

@Serializable
data class SetContactPropertyBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val config: ISetContactPropertyBlockConfig,
  override val exits: List<IBlockExit>
) : ISetContactPropertyBlock