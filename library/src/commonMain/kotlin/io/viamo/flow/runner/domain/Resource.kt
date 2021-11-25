import io.viamo.flow.runner.domain.IResourceWithContext
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IResourceValue
import kotlinx.serialization.Serializable

@Serializable
class Resource(
  override val uuid: String,
  override val values: List<IResourceValue>,
  override val context: Context
) : IResourceWithContext {

  private fun _getValueByContentType(contentType: SupportedContentType): String {
    val def = _findByContentType(contentType)
        ?: throw ResourceNotFoundException("""Unable to find resource for $contentType, ${context.language_id}, ${context.mode}""")

    return def.value
  }

  private fun _getValueByContentAndMimeType(contentType: SupportedContentType, mimeType: String): String {
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

  override fun getAudio() = _getValueByContentType(SupportedContentType.AUDIO)
  override fun getImage() = _getValueByContentType(SupportedContentType.IMAGE)

  override fun getText(): String {
    /**
     * TODO: was  EvaluatorFactory.create().evaluate(_getValueByContentType(SupportedContentType.TEXT), createEvalContextFrom(context))
     *  Make a Multiplatform lib for JS/JVM that uses the trick we use in Clipboard Android.
     *  * Use QuickJS to access EvaluatorFactory on JVM
     *  * Use KotlinJS lib to access ExperessionParser in JS.
     *  * Use both through the same Multiplatform Lib.
     */
    return "true" /*EvaluatorFactory.create().evaluate(_getValueByContentType(SupportedContentType.TEXT), createEvalContextFrom(context))*/
  }

  override fun getVideo() = _getValueByContentType(SupportedContentType.VIDEO)

  /**
   * Convenience replacement for getData("text/csv").
   * This should be deprecated and replaced with getData() to stick to the spec and be simpler.
   * @returns equivalent of this.getData("text/csv")
   */
  override fun getCsv() = getData("text/csv")
  override fun hasAudio() = _hasByContentType(SupportedContentType.AUDIO)
  override fun hasImage() = _hasByContentType(SupportedContentType.IMAGE)
  override fun hasText() = _hasByContentType(SupportedContentType.TEXT)
  override fun hasVideo() = _hasByContentType(SupportedContentType.VIDEO)
  override fun hasCsv() = hasData("text/csv")

  override fun get(key: SupportedContentType) = _getValueByContentType(key)
  override fun has(key: SupportedContentType) = _hasByContentType(key)

  fun getData(mimeType: String) = _getValueByContentAndMimeType(SupportedContentType.DATA, mimeType)
  fun hasData(mimeType: String): Boolean = _hasByContentAndMimeType(SupportedContentType.DATA, mimeType)
}
