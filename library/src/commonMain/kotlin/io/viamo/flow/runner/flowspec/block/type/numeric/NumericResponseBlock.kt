package io.viamo.flow.runner.flowspec.block.type.numeric

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val NUMERIC_TYPE = "MobilePrimitives.NumericResponse"

interface INumericResponseBlock : BlockContactEditable {
  override val config: INumericBlockConfig
}

@Serializable
@SerialName(NUMERIC_TYPE)
data class NumericResponseBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val exits: List<IBlockExit>,
  override val config: INumericBlockConfig
) : INumericResponseBlock