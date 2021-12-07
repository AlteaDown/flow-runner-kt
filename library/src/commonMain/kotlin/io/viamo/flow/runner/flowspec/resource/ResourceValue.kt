package io.viamo.flow.runner.flowspec.resource

import SupportedContentType
import io.viamo.flow.runner.flowspec.enums.SupportedMode
import kotlinx.serialization.Serializable

interface IResourceValue {
  val language_id: String
  val content_type: SupportedContentType
  val mime_type: String?
  val modes: List<SupportedMode>
  val value: String
}

@Serializable
data class ResourceValue(
  override val language_id: String,
  override val content_type: SupportedContentType,
  override val mime_type: String? = null,
  override val modes: List<SupportedMode>,
  override val value: String,
) : IResourceValue