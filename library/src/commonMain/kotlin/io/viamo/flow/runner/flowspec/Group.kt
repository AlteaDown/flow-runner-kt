package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.flowspec.contact.IContactGroup
import kotlinx.serialization.Serializable

@Serializable
open class Group(override val group_key: String, override val label: String?) : IGroup

@Serializable
class ContactGroup(
  override val group_key: String,
  override val label: String? = null,
  override var updated_at: String = createFormattedDate(),
  override var deleted_at: String? = null,
) : IGroup, IContactGroup {

  constructor(group: IGroup, updated_at: String = createFormattedDate(), deleted_at: String? = null) : this(
    group_key = group.group_key,
    label = group.label,
    updated_at = updated_at,
    deleted_at = deleted_at,
  )
}
