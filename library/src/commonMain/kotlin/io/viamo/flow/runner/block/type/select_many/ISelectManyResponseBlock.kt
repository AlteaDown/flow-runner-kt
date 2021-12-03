package io.viamo.flow.runner.block.type.select_many

import io.viamo.flow.runner.block.IBlockContactEditable
import io.viamo.flow.runner.block.IBlockExit
import io.viamo.flow.runner.block.IBlockUIMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

interface ISelectManyResponseBlock : IBlockContactEditable {
  override val config: ISelectManyResponseBlockConfig
}

@Serializable
data class SelectManyResponseBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val exits: List<IBlockExit>,
  override val config: ISelectManyResponseBlockConfig
) : ISelectManyResponseBlock