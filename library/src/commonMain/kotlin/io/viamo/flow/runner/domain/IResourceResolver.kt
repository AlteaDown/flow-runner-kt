package io.viamo.flow.runner.domain

import SupportedContentType
import SupportedContentType.TEXT
import io.viamo.flow.runner.flowspec.*

/**
 * io.viamo.flow.runner.flowspec.Resource definition: https://floip.gitbook.io/flow-specification/flows#resources
 *
 * Basically, a smarter version of an io.viamo.flow.runner."flow-spec".IResource with
 * her values having been filtered by (languageId, modes). */
interface IResourceWithContext : IResource {
  override val uuid: String
  override val values: List<IResourceValue>

  fun getText(context: Context): String
  fun hasText(): Boolean

  fun getAudio(context: Context): String
  fun hasAudio(): Boolean

  fun getImage(context: Context): String
  fun hasImage(): Boolean

  fun getVideo(context: Context): String
  fun hasVideo(): Boolean

  fun getCsv(context: Context): String
  fun hasCsv(): Boolean

  fun get(context: Context, key: SupportedContentType): String
  fun has(key: SupportedContentType): Boolean
}

interface IResourceResolver {
  val context: IContext

  fun resolve(resourceId: String): IResourceWithContext
}

fun createTextResourceVariantWith(value: String, ctx: IContext): ResourceValue {
  return ResourceValue(
    content_type = TEXT,
    value = value,
    language_id = ctx.language_id,
    modes = listOf(ctx.mode),
  )
}

/*fun getResource(context: IContext, resourceId: String): IResourceWithContext {
  return ResourceResolver(context).resolve(resourceId)
}*/
