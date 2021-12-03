package io.viamo.flow.runner.block.type.set_group_membership

import io.viamo.flow.runner.block.IBlockContactEditable
import io.viamo.flow.runner.block.IBlockExit
import io.viamo.flow.runner.block.IBlockUIMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

const val SET_GROUP_MEMBERSHIP_BLOCK_TYPE = "Core.SetGroupMembership"

interface ISetGroupMembershipBlock : IBlockContactEditable {
  override val config: ISetGroupMembershipBlockConfig
}


@Serializable
data class SetGroupMembershipBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val exits: List<IBlockExit>,
  override val config: ISetGroupMembershipBlockConfig
) : ISetGroupMembershipBlock