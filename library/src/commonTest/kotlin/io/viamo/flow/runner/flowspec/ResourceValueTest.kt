package io.viamo.flow.runner.flowspec

import SupportedContentType
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.enums.SupportedMode
import io.viamo.flow.runner.flowspec.resource.ResourceValue
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceValueTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    ResourceValue.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun ResourceValue.Companion.buildNoNulls(
  language_id: String = "language_id",
  content_type: SupportedContentType = SupportedContentType.TEXT,
  mime_type: String? = "mime_type",
  modes: List<SupportedMode> = listOf(
    SupportedMode.TEXT,
    SupportedMode.SMS,
    SupportedMode.USSD,
    SupportedMode.IVR,
    SupportedMode.RICH_MESSAGING,
    SupportedMode.OFFLINE,
  ),
  value: String = "value",
) = ResourceValue(
  language_id = language_id,
  content_type = content_type,
  mime_type = mime_type,
  modes = modes,
  value = value,
)