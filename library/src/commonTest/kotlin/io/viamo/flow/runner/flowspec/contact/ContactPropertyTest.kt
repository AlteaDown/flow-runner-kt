package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.contact.ContactPropertyType.ContactProperty
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ContactPropertyTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    ContactProperty.build().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun ContactProperty.Companion.build(
  contact_property_field_name: String = "contact_property_field_name",
  created_at: Instant = createFormattedDate(),
  updated_at: Instant = createFormattedDate(),
  deleted_at: Instant? = createFormattedDate(),
  value: String? = "value",
) = ContactProperty(
  contact_property_field_name = contact_property_field_name,
  created_at = created_at,
  updated_at = updated_at,
  deleted_at = deleted_at,
  value = value,
)
