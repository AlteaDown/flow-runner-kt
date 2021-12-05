package io.viamo.flow.runner.flowspec.block

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class CoordinatesTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Coordinates.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Coordinates.Companion.createNoNulls(
  x: Int = 0,
  y: Int = 0
) = Coordinates(
  x = x,
  y = y
)