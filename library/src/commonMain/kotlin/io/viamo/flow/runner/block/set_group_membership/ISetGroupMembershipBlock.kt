package io.viamo.flow.runner.block.set_group_membership

import io.viamo.flow.runner.flowspec.IBlock

const val SET_GROUP_MEMBERSHIP_BLOCK_TYPE = "Core.SetGroupMembership"

interface ISetGroupMembershipBlock : IBlock {
  override val config: ISetGroupMembershipBlockConfig
}
