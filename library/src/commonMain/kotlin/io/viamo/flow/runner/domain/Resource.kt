import io.viamo.flow.runner.domain.IResourceWithContext
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IResourceValue
import io.viamo.flow.runner.flowspec.createEvalContextFrom
import kotlinx.serialization.Serializable

@Serializable
class Resource(
  override val uuid: String,
  override val values: List<IResourceValue>,
  override val context: Context
) : IResourceWithContext {

  private fun _getValueByContentType(contentType: SupportedContentType): String {
    val def = this._findByContentType(contentType)
        ?: throw ResourceNotFoundException("""Unable to find resource for $contentType, ${context.language_id}, ${context.mode}""")

    return def.value
  }

  private fun _getValueByContentAndMimeType(contentType: SupportedContentType, mimeType: String): String {
    val def = this._findByContentAndMimeType(contentType, mimeType)
        ?: throw ResourceNotFoundException("Unable to find resource for $contentType, $mimeType, ${context.language_id}, ${context.mode}")

    return def.value
  }

  private fun _hasByContentType(contentType: SupportedContentType): Boolean {
    return this._findByContentType(contentType) != null
  }

  private fun _hasByContentAndMimeType(contentType: SupportedContentType, mimeType: String): Boolean {
    return this._findByContentAndMimeType(contentType, mimeType) != null
  }

  private fun _findByContentType(contentType: SupportedContentType): IResourceValue? {
    return this.values.find { it.content_type == contentType }
  }

  private fun _findByContentAndMimeType(contentType: SupportedContentType, mimeType: String): IResourceValue? {
    return this.values.find { it.content_type == contentType && it.mime_type == mimeType }
  }

  override fun getAudio() = this._getValueByContentType(SupportedContentType.AUDIO)
  override fun getImage() = this._getValueByContentType(SupportedContentType.IMAGE)

  override fun getText(): String {
    return EvaluatorFactory.create().evaluate(this._getValueByContentType(SupportedContentType.TEXT), createEvalContextFrom(this.context))
  }

  override fun getVideo() = this._getValueByContentType(SupportedContentType.VIDEO)

  /**
   * Convenience replacement for getData("text/csv").
   * This should be deprecated and replaced with getData() to stick to the spec and be simpler.
   * @returns equivalent of this.getData("text/csv")
   */
  override fun getCsv() = this.getData("text/csv")
  override fun hasAudio() = this._hasByContentType(SupportedContentType.AUDIO)
  override fun hasImage() = this._hasByContentType(SupportedContentType.IMAGE)
  override fun hasText() = this._hasByContentType(SupportedContentType.TEXT)
  override fun hasVideo() = this._hasByContentType(SupportedContentType.VIDEO)
  override fun hasCsv() = this.hasData("text/csv")

  override fun get(key: SupportedContentType) = this._getValueByContentType(key)
  override fun has(key: SupportedContentType) = this._hasByContentType(key)

  fun getData(mimeType: String) = this._getValueByContentAndMimeType(SupportedContentType.DATA, mimeType)
  fun hasData(mimeType: String): Boolean = this._hasByContentAndMimeType(SupportedContentType.DATA, mimeType)
}
