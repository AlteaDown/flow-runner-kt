package io.viamo.flow.runner.flowspec.block.type.print

import io.viamo.flow.runner.flowspec.block.BlockContactEditable
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.IBlockUIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val PRINT_TYPE = "ConsoleIO.Print"

interface IPrintBlock : BlockContactEditable {
  override val config: IPrintBlockConfig
}

@Serializable
@SerialName(PRINT_TYPE)
data class PrintBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val exits: List<IBlockExit>,
  override val config: IPrintBlockConfig
) : IPrintBlock