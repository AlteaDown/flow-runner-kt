package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Group.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Group.Companion.buildNoNulls(
  group_key: String = "group_key",
  label: String? = "label",
) = Group(
  group_key = group_key,
  label = label,
)