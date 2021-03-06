package io.viamo.flow.runner.flowspec.block.type.selectcase

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val CASE_TYPE = "Core.Case"

interface ICaseBlock : BlockContactEditable {
  override val config: ICaseBlockConfig
  override val exits: List<IBlockExit>
}

@Serializable
@SerialName(CASE_TYPE)
data class CaseBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val config: ICaseBlockConfig,
  override val exits: List<IBlockExit>
) : ICaseBlock
