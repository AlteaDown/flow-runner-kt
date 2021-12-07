package io.viamo.flow.runner.flowspec.block

import ValidationException
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

/**
 * Block Structure: https://floip.gitbook.io/flow-specification/flows#blocks
 */
interface IBlock {
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
  val tags: List<String>?

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
  //val type: String

  /**
   * Additional parameters that are specific to the type of the block. Details are provided within the Block documentation.
   */
  val config: IBlockConfig

  /**
   * a list of all the exits for the block.
   * Exits must contain the required keys below, and can contain additional keys based on the Block type
   */
  val exits: List<IBlockExit>

  fun findBlockExitWith(uuid: String): IBlockExit {
    return exits.firstOrNull { it.uuid == uuid }
        ?: throw IllegalStateException("Unable to find exit on block")
  }

  /**
   * @param block
   * @param context
   * @deprecated Use io.viamo.flow.runner."flow-spec".firstTrueOrNullBlockExitOrThrow or io.viamo.flow.runner."flow-spec".firstTrueBlockExitOrNull
   */
  fun findFirstTruthyEvaluatingBlockExitOn(): IBlockExit {
    check(exits.isNotEmpty()) { "Unable to find exits on block $uuid" }

    /* TODO: Was
    *   val evalContext = createEvalContextFrom(context)
    *   return exits.find { exit -> !(exit.default ?: false) && evaluateToBool(exit.test ?: error("exit.test was null"), evalContext) }*/
    return exits.first()
  }

  fun firstTrueBlockExitOrNull(): IBlockExit? {
    return try {
      firstTrueOrNullBlockExitOrThrow()
    } catch (e: Exception) {
      null
    }
  }

  fun firstTrueOrNullBlockExitOrThrow(): IBlockExit {
    return firstBlockExit()
        ?: throw  ValidationException("All block exits evaluated to false. Block: $uuid")
  }

  fun firstBlockExit(): IBlockExit? {
    return try {
      /* TODO: Was
      *   val evalContext = createEvalContextFrom(context)
      *   block.exits.find { blockExit -> evaluateToBool(blockExit.test.toString(), evalContext) } ?: findDefaultBlockExitOnOrNull(block)*/
      exits.firstOrNull()
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOnOrNull()
    }
  }

  fun findDefaultBlockExitOnOrNull(): IBlockExit? {
    return try {
      findDefaultBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      null
    }
  }

  fun findDefaultBlockExitOrThrow(): IBlockExit {
    /* We have to test against null, as some default exits are being sent with a value of null (MessageBlock, SetGroupMembershipBlock, CaseBlock)*/
    return exits.find { blockExit -> blockExit.default ?: false || blockExit.test == null }
        ?: throw ValidationException("Unable to find default exit on block $uuid")
  }

  fun isLastInFlow() = exits.all { it.destination_block == null }
}

/**
 * The type field is restricted to Kotlinx.Serialization (and it will determine what class to Serialize to based on it), but
 * the field is not accessible in Kotlin. However, it's possible to get it, via the Block's SerializationDescriptor.
 */
val IBlock.type: String
  get() = JSON.serializersModule.getPolymorphic(IBlock::class, this)?.descriptor?.serialName
      ?: error("Block.type was not set, or you have a Block Class that is missing the @SerialName annotation")

fun evaluateToBool(expr: String, ctx: IContext): Boolean {
  return Json.parseToJsonElement(evaluateToString(expr, ctx).lowercase()).jsonPrimitive.boolean
}

fun evaluateToString(expr: String, ctx: IContext): String {
  // TODO: Was EvaluatorFactory.create().evaluate(wrapInExprSyntaxWhenAbsent(expr), ctx)
  return "true"
}

fun wrapInExprSyntaxWhenAbsent(expr: String) = if (expr.startsWith("@(")) expr else "@($expr)"

interface BlockContactEditable : IBlock {
  override val config: IBlockConfigContactEditable

  /**
   * Set a property on the contact contained in the flow context.
   */
  fun setContactProperty(context: Context) {
    config.let {
      val setContactProperty = it.set_contact_property
      if (setContactProperty != null) {
        context.contact.setSingleContactProperty(setContactProperty, context)
      }
    }
  }
}