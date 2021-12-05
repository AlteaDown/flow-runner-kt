package io.viamo.flow.runner.flowspec.contact

import kotlinx.datetime.Instant

interface IContactProperty {
  val contact_property_field_name: String
  val created_at: Instant
  val updated_at: Instant
  val deleted_at: Instant?
  val __value__: String?
  val value: String?
}
