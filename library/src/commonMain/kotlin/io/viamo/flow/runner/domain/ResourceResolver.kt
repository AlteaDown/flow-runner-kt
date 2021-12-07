import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IContext
import io.viamo.flow.runner.flowspec.resource.IResource
import io.viamo.flow.runner.flowspec.resource.IResourceValue
import io.viamo.flow.runner.flowspec.resource.ResourceValue

val UUID_MATCHER = Regex("""/[\d\w]{8}(-[\d\w]{4}){3}-[\d\w]{12}/i""")

fun isUUID(uuid: String): Boolean = uuid.length == 36 && UUID_MATCHER.matches(uuid)
/*
class ResourceResolver(override val context: IContext) : IResourceResolver {

  fun resolve(resourceId: String): IResourceWithContext {
    val mode = this.context.mode
    val language_id = this.context.language_id

    if (!isUUID(resourceId)) {
      return io.viamo.flow.runner.flowspec.resource.Resource(resourceId, listOf(createTextResourceVariantWith(resourceId, this.context)), this.context)
    }

    val resource = this.context.resources.firstOrNull { it.uuid == resourceId }
        ?: throw ResourceNotFoundException("""No resource matching $resourceId for {$mode, $language_id}""")

    val values = resource.values.filter { it.language_id == language_id && it.modes.intersect(mode).isNotEmpty() }

    return io.viamo.flow.runner.flowspec.resource.Resource(resourceId, values, this.context)
  }
}*/

/*
object ResourceResolver {
  val duktape: Duktape by lazy {
    Duktape.create().apply {
      evaluate(getJsFile(weakContext?.get() ?: error("context was not set"), "expression-evaluator.js"))
      evaluate(getJsFile(weakContext?.get() ?: error("context was not set"), "ClipboardReact-expressions-parser.js"))
      evaluate("""var evaluator = this['@floip/expression-evaluator'].create()""".trimIndent())
    }
  }

  suspend fun resolveValue(
    context: Context,
    orgId: String,
    resourceUuid: String,
    languageId: String,
    contentType: ContentType,
    flowRunnerContext: FlowRunnerContext? = null,
  ): String? {
    return resolve(context, orgId, resourceUuid, languageId, contentType, flowRunnerContext)?.value
  }

  suspend fun resolve(
    context: Context,
    orgId: String,
    resourceUuid: String,
    languageId: String,
    contentType: ContentType,
    flowRunnerContext: FlowRunnerContext? = null,
  ): ResourceValueEntity? {
    val mode = io.viamo.flow.runner.flowspec.enums.SupportedMode.OFFLINE

    val foundResourceValue = Db.resources.getByUuid(resourceUuid)
      ?.resourceValues
      ?.filter { it.languageId == languageId && it.modes.contains(mode.value) }
      ?.firstOrNull { it.contentType == contentType }

    return if (foundResourceValue == null && contentType == TEXT) {
      ResourceValueEntity(
        uuid = resourceUuid,
        contentType = contentType,
        modes = listOf(io.viamo.flow.runner.flowspec.enums.SupportedMode.OFFLINE.value),
        value = resourceUuid,
        resourceUuid = resourceUuid,
        orgId = orgId,
        languageId = languageId,
      )
    } else if (foundResourceValue != null && contentType == TEXT) {
      flowRunnerContext?.let {
        ResourceValueEntity(
          uuid = resourceUuid,
          contentType = contentType,
          modes = listOf(io.viamo.flow.runner.flowspec.enums.SupportedMode.OFFLINE.value),
          value = evaluateExpression(context, foundResourceValue.value, it),
          resourceUuid = resourceUuid,
          orgId = orgId,
          languageId = languageId,
        )
      } ?: foundResourceValue
    } else {
      foundResourceValue
    }
  }

  private suspend fun evaluateExpression(context: Context, expression: String, flowRunnerContext: FlowRunnerContext): String {
    this.weakContext = WeakReference(context)

    return withContext(Dispatchers.Default) {
      val evalContext = createEvalContextFrom(flowRunnerContext).toJsonString()
      try {
        duktape.evaluate("""var evaluator = this['@floip/expression-evaluator'].create()""")
        val evaluatorFactory = duktape.get("evaluator", IEvaluator::class.java)
        evaluatorFactory.evaluate(
          expression,
          context = evalContext
        )
      } catch (exception: Throwable) {
        Timber.e("Encountered problem with Expression: $expression")
        throw Error("Encountered problem with Expression: $expression ;\n Context was: $evalContext", exception)
      }
    }
  }

  private fun getJsFile(context: Context, fileName: String): String {
    return context.assets.open(fileName).use {
      val charset = Charset.forName("UTF-8")
      it.readBytes().toString(charset)
    }
  }
}*/

/*
// todo: push eval stuff into `Expression.evaluate()` abstraction for evalContext + result handling 👇
fun createEvalContextFrom(context: FlowRunnerContext): IContext {
  val contact = context.contact
  val cursor = context.cursor
  val mode = context.mode
  val currentLanguage = context.language_id

  var currentFlow: Flow? = null
  var currentBlock: Block? = null
  var currentPrompt: JsonObject? = null

  if (cursor != null) {
    // because evalContext.block references the current block we're working on
    currentFlow = context.flows.first { it.uuid == context.interactions.last().flow_id }
    currentBlock = context.flows.asSequence()
      .mapNotNull { flow -> flow.blocks.firstOrNull { it.uuid == context.interactions.last().block_id } }
      .first()
    currentPrompt = cursor.promptConfig
  }

  return mapOf(
    "contact" to getContactContext(contact),
    "channel" to Json.encodeToJsonElement(SupportedMode.serializer(), mode),
    "flow" to getFlowContext(currentFlow, currentLanguage, context),
    "block" to currentBlock.let { block -> Json.encodeToJsonElement(BlockWithValue(block, currentPrompt?.get("value") ?: JsonNull)) },
    "date" to getDateContext(),
  ).toJsonElement()
}

private fun getContactContext(contact: JsonObject): Map<String, *> {
  return contact.let {
    mapOf(
      *it.map { entry ->
        if (entry.key == "groups") {
          entry.key to entry.value.jsonArray.filter { group -> group.jsonObject["deleted_at"]?.jsonPrimitive == null }
        } else {
          entry.key to entry.value
        }
      }.toTypedArray()
    )
  }
}

private fun getFlowContext(
  currentFlow: Flow?,
  currentLanguage: String,
  context: FlowRunnerContext,
): Map<String, *> {
  return currentFlow.run {
    requireNotNull(this)

    mapOf(
      "uuid" to uuid,
      "name" to name,
      "last_modified" to DateTimeFormatter.ISO_INSTANT.format(last_modified.toInstant()),
      "interaction_timeout" to interaction_timeout,
      "vendor_metadata" to vendor_metadata,
      "supported_modes" to supported_modes.map { it },
      "languages" to languages.map { language -> Json.encodeToJsonElement(language) },
      "first_block_id" to first_block_id,
      "resources" to resources.map { resource -> Json.encodeToJsonElement(resource) },
      "blocks" to blocks.map { block -> Json.encodeToJsonElement(block) },
      "label" to label,
      currentLanguage to currentLanguage,
      *context.flows.flatMap { flow -> flow.blocks }
        .map { block ->
          block.name to Json.encodeToJsonElement(
            BlockWithValue(
              block,
              context.interactions.lastOrNull { interaction -> interaction.block_id == block.uuid }?.value ?: JsonNull
            )
          )
        }
        .toTypedArray()
    )
  }
}

private fun getDateContext(): Map<String, *> {
  val now = Clock.System.now()

  return mapOf(
    "today" to now.toUtcDate().toIso8601(),
    "yesterday" to now.toUtcDate().minusDays(1).toIso8601(),
    "tomorrow" to now.toUtcDate().plusDays(1).toIso8601(),
    "now" to now.toUtcDateTime().toIso8601(),
    "__value__" to now.toUtcDateTime().toIso8601(),
  )
}

@Serializable
open class BlockWithValue(
  val uuid: String? = null,
  val name: String? = null,
  val type: String? = null,
  val label: String? = null,
  val semantic_label: String? = null,
  val vendor_metadata: JsonObject? = null,
  val exits: List<BlockExit>? = null,
  val config: JsonElement? = null,
  val value: JsonElement,
  val __value__: JsonElement,
) {
  constructor(block: Block?, value: JsonElement) : this(
    uuid = block?.uuid,
    name = block?.name,
    type = block?.type,
    label = block?.label,
    semantic_label = block?.semantic_label,
    vendor_metadata = block?.vendor_metadata,
    exits = block?.exits,
    config = block?.config,
    value = value,
    __value__ = value,
  )
}*/
/**
 * io.viamo.flow.runner.flowspec.resource.Resource definition: https://floip.gitbook.io/flow-specification/flows#resources
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
    content_type = SupportedContentType.TEXT,
    value = value,
    language_id = ctx.language_id,
    modes = listOf(ctx.mode),
  )
}