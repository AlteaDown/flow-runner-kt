package io.viamo.flow.runner.flowspec.block.type.message

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageBlockConfigTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    MessageBlockConfig.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(Json.encodeToString(original)))
    }
  }
}

fun MessageBlockConfig.Companion.createNoNulls(prompt: String = "prompt") = MessageBlockConfig(prompt = prompt)