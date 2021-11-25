package io.viamo.flow.runner.model.block

import io.viamo.flow.runner.flowspec.IBlock

val SET_GROUP_MEMBERSHIP_BLOCK_TYPE = "Core.SetGroupMembership"

interface ISetGroupMembershipBlock : IBlock<ISetGroupMembershipBlockConfig> {
  val config: ISetGroupMembershipBlockConfig
}
