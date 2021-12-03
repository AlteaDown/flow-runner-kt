package io.viamo.flow.runner.block.type.message

import io.viamo.flow.runner.ISerializableTest
import io.viamo.flow.runner.ext.JSON
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageBlockConfigTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    MessageBlockConfig.create().let { original ->
      assertEquals(original, JSON.decodeFromString(Json.encodeToString(original)))
    }
  }
}

fun MessageBlockConfig.Companion.create(prompt: String = "prompt") = MessageBlockConfig(prompt = prompt)