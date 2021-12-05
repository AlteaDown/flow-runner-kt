package io.viamo.flow.runner.flowspec

import kotlinx.serialization.Serializable

interface IGroup {
  val group_key: String
  val label: String?
  val __value__: String
    get() = group_key
}

@Serializable
data class Group(
  override val group_key: String,
  override val label: String?,
) : IGroup
