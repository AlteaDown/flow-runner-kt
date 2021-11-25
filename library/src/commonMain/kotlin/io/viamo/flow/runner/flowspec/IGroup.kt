package io.viamo.flow.runner.flowspec

interface IGroup {
  val group_key: String
  val label: String?
  val __value__: String
    get() = group_key
}

fun isGroup(thing: Any) = thing is IGroup
