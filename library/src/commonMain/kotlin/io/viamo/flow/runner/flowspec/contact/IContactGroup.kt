package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.flowspec.IGroup

interface IContactGroup : IGroup {
  var updated_at: String
  var deleted_at: String?
}
