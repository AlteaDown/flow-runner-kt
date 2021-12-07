package io.viamo.flow.runner.flowspec.block.type.select_many

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val SELECT_MANY_TYPE = "MobilePrimitives.SelectManyResponse"

interface ISelectManyResponseBlock : BlockContactEditable {
  override val config: ISelectManyResponseBlockConfig
}

@Serializable
@SerialName(SELECT_MANY_TYPE)
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