package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.flowspec.IGroup
import kotlinx.datetime.Instant

interface IContactGroup : IGroup {
  var updated_at: Instant
  var deleted_at: Instant?
}
