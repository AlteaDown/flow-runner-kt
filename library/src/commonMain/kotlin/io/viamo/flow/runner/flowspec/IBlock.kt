package io.viamo.flow.runner.flowspec

import BlockWithValue
import ValidationException
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.ext.toJsonElement
import io.viamo.flow.runner.ext.toUtcDate
import io.viamo.flow.runner.model.block.IBlockConfig
import io.viamo.flow.runner.model.block.SetContactProperty
import kotlinx.datetime.*
import kotlinx.datetime.DateTimeUnit.DayBased
import kotlinx.serialization.json.*

/*val module = SerializersModule {
  polymorphic(IBlock::class) {
    subclass(OwnedProject::class)
  }
}*/

/**
 * Block Structure: https://floip.gitbook.io/flow-specification/flows#blocks
 */
interface IBlock<out BLOCK_CONFIG : IBlockConfig> {
  /**
   * A globally unique identifier for this Block.  (See UUID Format: https://floip.gitbook.io/flow-specification/flows#uuid-format)
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val uuid: String

  /**
   * A human-readable "variable name" for this block.
   * This must be restricted to word characters so that it can be used as a variable name in expressions.
   * When blocks write results output, they write to a variable corresponding the name of the block.
   *
   * @pattern ^[a-zA-Z_]\w*$
   */
  val name: String

  /**
   * A human-readable free-form description for this Block.
   */
  val label: String?

  /**
   * A user-controlled field that can be used to code the meaning of the data collected by this block in a standard taxonomy or
   * coding system, * e.g.: a FHIR ValueSet, an industry-specific coding system like SNOMED CT,
   * or an organization's internal taxonomy service. (e.g. "SNOMEDCT::Gender finding")
   */
  val semantic_label: String?

  /**
   * an arbitrary list of Strings for categorization of the block's content, meaning, etc.
   * This has a similar purpose to semantic_label, but the assumption is that many related blocks
   * might have the same tags.
   */
  val tags: Array<String>?

  /**
   * A set of key-value elements that is not controlled by the Specification,
   * but could be relevant to a specific vendor/platform/implementation.
   */
  val vendor_metadata: Map<String, Any>?

  /**
   * A set of key-value records describing information about how blocks are displayed on a UI/flowchart editor
   */
  val ui_metadata: IBlockUIMetadata

  /**
   * A specific String designating the type or "subclass" of this Block.
   * This must be one of the Block type names within the specification, such as Core.RunFlow or MobilePrimitives.Message.
   */
  val type: String

  /**
   * Additional parameters that are specific to the type of the block. Details are provided within the Block documentation.
   */
  val config: BLOCK_CONFIG

  /**
   * a list of all the exits for the block.
   * Exits must contain the required keys below, and can contain additional keys based on the Block type
   */
  val exits: List<IBlockExit>
}

/**
 * Coordinates indicating location of this block on the Flow Builder's canvas
 */
interface IBlockUIMetadataCanvasCoordinates {
  val x: Number
  val y: Number
}

/**
 * A set of key-value records describing information about how blocks are displayed on a UI/flowchart editor
 */
interface IBlockUIMetadata : Map<String, Any> {
  val canvas_coordinates: IBlockUIMetadataCanvasCoordinates
}

fun findBlockExitWith(uuid: String, block: IBlock<*>): IBlockExit {
  val exit = block.exits.firstOrNull { it.uuid == uuid }
  checkNotNull(exit) { "Unable to find exit on block" }

  return exit
}

/**
 * @param block
 * @param context
 * @deprecated Use io.viamo.flow.runner."flow-spec".firstTrueOrNullBlockExitOrThrow or io.viamo.flow.runner."flow-spec".firstTrueBlockExitOrNull
 */
fun findFirstTruthyEvaluatingBlockExitOn(block: IBlock<*>, context: IContext): IBlockExit? {
  val exits = block.exits
  if (exits.isEmpty()) {
    throw ValidationException("Unable to find exits on block ${block.uuid}")
  }

  val evalContext = createEvalContextFrom(context)
  return exits.find { exit -> !(exit.default ?: false) && evaluateToBool(exit.test ?: error("exit.test was null"), evalContext) }
}
//({ test, default: isDefault = false }

fun firstTrueBlockExitOrNull(block: IBlock<*>, context: IContext): IBlockExit? {
  return try {
    firstTrueOrNullBlockExitOrThrow(block, context)
  } catch (e: Exception) {
    null
  }
}

fun firstTrueOrNullBlockExitOrThrow(block: IBlock<*>, context: IContext): IBlockExit {
  return _firstBlockExit(context, block) ?: throw  ValidationException("All block exits evaluated to false. Block: ${block.uuid}")
}

fun _firstBlockExit(context: IContext, block: IBlock<*>): IBlockExit? {
  return try {
    val evalContext = createEvalContextFrom(context)
    block.exits.find { blockExit ->
      evaluateToBool(blockExit.test.toString(), evalContext)
    } ?: findDefaultBlockExitOnOrNull(block)
  } catch (e: Throwable) {
    e.printStackTrace()
    findDefaultBlockExitOnOrNull(block)
  }
}

fun findDefaultBlockExitOnOrNull(block: IBlock<*>): IBlockExit? {
  try {
    return findDefaultBlockExitOrThrow(block)
  } catch (e: Throwable) {
    e.printStackTrace()
    return null
  }
}

fun findDefaultBlockExitOrThrow(block: IBlock<*>): IBlockExit {
  /* We have to test against null, as some default exits are being sent with a value of null
      (MessageBlock, SetGroupMembershipBlock, CaseBlock)*/
  return block.exits.find { blockExit -> blockExit.default ?: false || blockExit.test == null }
      ?: throw ValidationException("Unable to find default exit on block ${block.uuid}")
}

fun isLastBlock(block: IBlock<*>): Boolean {
  return block.exits.all { it.destination_block == null }
}

interface IEvalContextBlock {
  val __value__: Any
  val time: String
  val __interactionId: String
  val value: Any
  val text: String
}

typealias TEvalContextBlockMap = Map<String, IEvalContextBlock>

// TODO: Define/Return a (EvalContext: IContext) instead of JsonObject
fun createEvalContextFrom(context: IContext): JsonObject {
  val contact = context.contact
  val cursor = context.cursor
  val mode = context.mode
  val currentLanguage = context.language_id

  var currentFlow: IFlow? = null
  var currentBlock: IBlock<*>? = null
  var currentPrompt: IPromptConfig? = null

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
    "block" to currentBlock.let { block -> Json.encodeToJsonElement(BlockWithValue(block, currentPrompt?.value ?: JsonNull)) },
    "date" to getDateContext(),
  ).toJsonElement()
}

private fun getContactContext(contact: IContact): IContact {
  return contact.also { it.groups = it.groups.filter { group -> group.deleted_at == null } }
}

// TODO: Define/Return a (FlowEvalContext: IFlow) instead of JsonObject
private fun getFlowContext(
  currentFlow: IFlow?,
  currentLanguage: String,
  context: IContext,
): Map<String, *> {
  return currentFlow.run {
    requireNotNull(this)

    mapOf(
      "uuid" to uuid,
      "name" to name,
      "last_modified" to last_modified.toInstant(),
      "interaction_timeout" to interaction_timeout,
      "vendor_metadata" to vendor_metadata,
      "supported_modes" to supported_modes.map { it },
      "languages" to languages.map { language -> Json.encodeToJsonElement(language) },
      "first_block_id" to first_block_id,
      "resources" to context.resources.map { resource -> Json.encodeToJsonElement(resource) },
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
  return Clock.System.now().let { now ->
    mapOf(
      "today" to now.toUtcDate(),
      "yesterday" to now.toUtcDate().minus(DayBased(1)),
      "tomorrow" to now.toUtcDate().plus(DayBased(1)),
      "now" to now,
      "__value__" to now,
    )
  }
}

fun evaluateToBool(expr: String, ctx: IContext): Boolean {
  return Json.parseToJsonElement(evaluateToString(expr, ctx).lowercase()).jsonPrimitive.boolean
}

fun evaluateToString(expr: String, ctx: IContext): String {
  return EvaluatorFactory.create().evaluate(wrapInExprSyntaxWhenAbsent(expr), ctx)
}

fun wrapInExprSyntaxWhenAbsent(expr: String): String {
  return if (expr.startsWith("@(")) expr else """@(${expr})"""
}

/**
 * Set a property on the contact contained in the flow context.
 */
fun setContactProperty(
  block: IBlock<*>,
  context: IContext,
) {
  val setContactProperty = block.config.set_contact_property
  if (setContactProperty != null) {
    setSingleContactProperty(setContactProperty, context)
  }
}

fun setSingleContactProperty(property: SetContactProperty, context: IContext) {
  val value = evaluateToString(property.property_value, createEvalContextFrom(context))
  context.contact.setProperty(property.property_key, value)
}
