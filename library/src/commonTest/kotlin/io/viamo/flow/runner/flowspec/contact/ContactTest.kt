package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.ContactGroup
import io.viamo.flow.runner.flowspec.createNoNulls
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ContactTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Contact.create().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Contact.Companion.create(
  id: String = "1234",
  properties: MutableMap<String, ContactPropertyType> = mutableMapOf("name" to ContactPropertyType.ContactProperty.create()),
  groups: MutableList<ContactGroup> = mutableListOf(ContactGroup.createNoNulls()),
) = Contact(
  id = id,
  properties = properties,
  groups = groups,
)