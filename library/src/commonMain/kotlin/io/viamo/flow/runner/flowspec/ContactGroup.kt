package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.flowspec.contact.IContactGroup
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ContactGroup(
  override val group_key: String,
  override var updated_at: Instant,
  override val label: String? = null,
  override var deleted_at: Instant? = null,
) : IContactGroup {

  constructor(
    group: IGroup,
    updated_at: Instant = createFormattedDate(),
    deleted_at: Instant? = null
  ) : this(
    group_key = group.group_key,
    label = group.label,
    updated_at = updated_at,
    deleted_at = deleted_at,
  )
}