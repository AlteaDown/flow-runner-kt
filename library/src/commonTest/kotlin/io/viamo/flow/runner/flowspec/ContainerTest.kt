package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.resource.Resource
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class ContainerTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Container.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Container.Companion.buildNoNulls(
  specification_version: String = "specification_version",
  uuid: String = "uuid",
  name: String = "name",
  description: String? = "description",
  vendor_metadata: JsonObject? = JsonObject(emptyMap()),
  flows: List<Flow> = listOf(Flow.buildNoNulls()),
  resources: List<Resource> = listOf(Resource.buildNoNulls()),
) = Container(
  specification_version = specification_version,
  uuid = uuid,
  name = name,
  description = description,
  vendor_metadata = vendor_metadata,
  flows = flows,
  resources = resources,
)