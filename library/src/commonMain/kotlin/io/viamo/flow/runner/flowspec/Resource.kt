package io.viamo.flow.runner.flowspec

import ResourceNotFoundException
import SupportedContentType
import io.viamo.flow.runner.domain.IResourceWithContext
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

interface IResource {
  val uuid: String

  // each describes the resource content to use for a content_type
  val values: List<IResourceValue>
}

@Serializable
data class Resource(
  override val uuid: String,
  override val values: List<ResourceValue>,
) : IResourceWithContext {

  private fun _getValueByContentType(context:Context, contentType: SupportedContentType): String {
    val def = _findByContentType(contentType)
        ?: throw ResourceNotFoundException("""Unable to find resource for $contentType, ${context.language_id}, ${context.mode}""")

    return def.value
  }

  private fun _getValueByContentAndMimeType(context:Context, contentType: SupportedContentType, mimeType: String): String {
    val def = _findByContentAndMimeType(contentType, mimeType)
        ?: throw ResourceNotFoundException("Unable to find resource for $contentType, $mimeType, ${context.language_id}, ${context.mode}")

    return def.value
  }

  private fun _hasByContentType(contentType: SupportedContentType): Boolean {
    return _findByContentType(contentType) != null
  }

  private fun _hasByContentAndMimeType(contentType: SupportedContentType, mimeType: String): Boolean {
    return _findByContentAndMimeType(contentType, mimeType) != null
  }

  private fun _findByContentType(contentType: SupportedContentType): IResourceValue? {
    return values.find { it.content_type == contentType }
  }

  private fun _findByContentAndMimeType(contentType: SupportedContentType, mimeType: String): IResourceValue? {
    return values.find { it.content_type == contentType && it.mime_type == mimeType }
  }

  override fun getAudio(context:Context, ) = _getValueByContentType(context, SupportedContentType.AUDIO)
  override fun getImage(context:Context, ) = _getValueByContentType(context, SupportedContentType.IMAGE)

  override fun getText(context:Context, ): String {
    /**
     * TODO: was  EvaluatorFactory.create().evaluate(_getValueByContentType(SupportedContentType.TEXT), createEvalContextFrom(context))
     *  Make a Multiplatform lib for JS/JVM that uses the trick we use in Clipboard Android.
     *  * Use QuickJS to access EvaluatorFactory on JVM
     *  * Use KotlinJS lib to access ExperessionParser in JS.
     *  * Use both through the same Multiplatform Lib.
     */
    return "true" /*EvaluatorFactory.create().evaluate(_getValueByContentType(SupportedContentType.TEXT), createEvalContextFrom(context))*/
  }

  override fun getVideo(context:Context) = _getValueByContentType(context, SupportedContentType.VIDEO)

  /**
   * Convenience replacement for getData("text/csv").
   * This should be deprecated and replaced with getData() to stick to the spec and be simpler.
   * @returns equivalent of this.getData("text/csv")
   */
  override fun getCsv(context:Context, ) = getData(context,"text/csv")
  override fun hasAudio() = _hasByContentType(SupportedContentType.AUDIO)
  override fun hasImage() = _hasByContentType(SupportedContentType.IMAGE)
  override fun hasText() = _hasByContentType(SupportedContentType.TEXT)
  override fun hasVideo() = _hasByContentType(SupportedContentType.VIDEO)
  override fun hasCsv() = hasData("text/csv")

  override fun get(context:Context, key: SupportedContentType) = _getValueByContentType(context, key)
  override fun has(key: SupportedContentType) = _hasByContentType(key)

  fun getData(context:Context, mimeType: String) = _getValueByContentAndMimeType(context, SupportedContentType.DATA, mimeType)
  fun hasData(mimeType: String): Boolean = _hasByContentAndMimeType(SupportedContentType.DATA, mimeType)
}