package io.viamo.flow.runner.flowspec.block

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockUiMetadataTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    BlockUIMetadata.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun BlockUIMetadata.Companion.createNoNulls(
  coordinates: Coordinates = Coordinates.createNoNulls()
) = BlockUIMetadata(
  canvas_coordinates = coordinates
)