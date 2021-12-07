package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.contact.ContactGroup
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ContactGroupTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    ContactGroup.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun ContactGroup.Companion.buildNoNulls(
  group_key: String = "group_key",
  label: String? = "label",
  updated_at: Instant = createFormattedDate(),
  deleted_at: Instant? = createFormattedDate(),
) = ContactGroup(
  group_key = group_key,
  label = label,
  updated_at = updated_at,
  deleted_at = deleted_at,
)

fun ContactGroup.Companion.createWithGroup(
  group: IGroup,
  updated_at: Instant = createFormattedDate(),
  deleted_at: Instant? = null
) = ContactGroup(
  group = group,
  updated_at = updated_at,
  deleted_at = deleted_at,
)