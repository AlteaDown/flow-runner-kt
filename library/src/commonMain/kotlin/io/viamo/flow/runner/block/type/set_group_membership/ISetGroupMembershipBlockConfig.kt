package io.viamo.flow.runner.block.type.set_group_membership

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface ISetGroupMembershipBlockConfig : IBlockConfigContactEditable {
  /**
   * An identifier for the group that membership will be set within.
   */
  val group_key: String

  /**
   * A human-readable label in addition to the group_key, in cases where the group_name needs to be displayed to the io.viamo.flow.runner."flow-spec".Contact.
   */
  val group_name: String?

  /**
   * Determines the membership state: falsy to remove the contact from the group,
   * truthy to add, and null for no change to the existing membership.
   */
  val is_member: String
}
