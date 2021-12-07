package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.resource.Resource
import io.viamo.flow.runner.flowspec.resource.ResourceValue
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Resource.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Resource.Companion.buildNoNulls(
  uuid: String = "uuid",
  values: List<ResourceValue> = listOf(ResourceValue.buildNoNulls()),
) = Resource(
  uuid = uuid,
  values = values,
)