package io.viamo.flow.runner.flowspec.block.type.advanced_select_one

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val ADVANCED_SELECT_ONE_BLOCK_TYPE = "MobilePrimitives.Extended.AdvancedSelectOne"

interface IAdvancedSelectOneBlock : BlockContactEditable {
  override val config: IAdvancedSelectOneBlockConfig
}

@Serializable
data class AdvancedSelectOneBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val config: IAdvancedSelectOneBlockConfig,
  override val exits: List<IBlockExit>,
) : IAdvancedSelectOneBlock
