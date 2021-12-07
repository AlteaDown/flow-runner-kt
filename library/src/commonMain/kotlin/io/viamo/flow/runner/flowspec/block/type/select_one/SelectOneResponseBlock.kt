package io.viamo.flow.runner.flowspec.block.type.select_one

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val SELECT_ONE_TYPE = "MobilePrimitives.SelectOneResponse"

// todo: currently we don't perform any other behaviour than test evaluation on SelectOne
interface ISelectOneResponseBlock : BlockContactEditable {
  override val config: ISelectOneResponseBlockConfig
}

@Serializable
@SerialName(SELECT_ONE_TYPE)
data class SelectOneResponseBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val exits: List<IBlockExit>,
  override val config: ISelectOneResponseBlockConfig
) : ISelectOneResponseBlock