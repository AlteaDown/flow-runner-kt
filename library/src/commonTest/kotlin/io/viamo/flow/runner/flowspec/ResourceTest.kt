package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Resource.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Resource.Companion.createNoNulls(
  uuid: String = "uuid",
  values: List<ResourceValue> = listOf(ResourceValue.createNoNulls()),
) = Resource(
  uuid = uuid,
  values = values,
)