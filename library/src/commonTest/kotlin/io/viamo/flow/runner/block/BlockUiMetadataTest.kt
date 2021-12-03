package io.viamo.flow.runner.block

import io.viamo.flow.runner.ISerializableTest
import io.viamo.flow.runner.ext.JSON
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockUiMetadataTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    BlockUIMetadata.create().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun BlockUIMetadata.Companion.create(
  coordinates: Coordinates = Coordinates.create()
) = BlockUIMetadata(
  canvas_coordinates = coordinates
)