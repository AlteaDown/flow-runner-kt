package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.buildNoNulls
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ContactTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Contact.build().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Contact.Companion.build(
  id: String = "1234",
  properties: MutableMap<String, ContactPropertyType> = mutableMapOf("name" to ContactPropertyType.ContactProperty.build()),
  groups: MutableList<ContactGroup> = mutableListOf(ContactGroup.buildNoNulls()),
) = Contact(
  id = id,
  properties = properties,
  groups = groups,
)