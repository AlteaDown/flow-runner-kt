package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Language.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Language.Companion.buildNoNulls(
  id: String = "id",
  label: String? = "label",
  iso_639_3: String = "iso_639_3",
  variant: String? = "variant",
  bcp_47: String? = "bcp_47"
) = Language(
  id = id,
  label = label,
  iso_639_3 = iso_639_3,
  variant = variant,
  bcp_47 = bcp_47,
)