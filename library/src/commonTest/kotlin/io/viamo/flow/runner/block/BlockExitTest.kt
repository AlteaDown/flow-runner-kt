package io.viamo.flow.runner.block

import io.viamo.flow.runner.ISerializableTest
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.ext.toJsonPrimitive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockExitTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    BlockExit.create().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun BlockExit.Companion.create(
  uuid: String = "0",
  name: String = "name",
  destination_block: String? = "",
  semantic_label: String? = "null",
  test: String? = "",
  config: JsonObject = JsonObject(mapOf("field" to "value".toJsonPrimitive())),
  default: Boolean? = false
) = BlockExit(
  uuid = uuid,
  name = name,
  destination_block = destination_block,
  semantic_label = semantic_label,
  test = test,
  config = config,
  default = default,
)