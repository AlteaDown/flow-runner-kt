package io.viamo.flow.runner.flowspec

import SupportedContentType

interface IResourceValue {
  val language_id: String
  val content_type: SupportedContentType
  val mime_type: String?
  val modes: List<SupportedMode>
  val value: String
}

data class ResourceValue(
  override val language_id: String,
  override val content_type: SupportedContentType,
  override val mime_type: String? = null,
  override val modes: List<SupportedMode>,
  override val value: String,
) : IResourceValue

interface IResource {
  val uuid: String

  // each describes the resource content to use for a content_type
  val values: List<IResourceValue>
}
