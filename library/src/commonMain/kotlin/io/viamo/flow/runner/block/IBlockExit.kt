package io.viamo.flow.runner.block

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Block Exit: https://floip.gitbook.io/flow-specification/flows#exit-node-specification
 */
interface IBlockExit {
  /**
   * A globally unique identifier for this Block.  (See UUID Format: https://floip.gitbook.io/flow-specification/flows#uuid-format)
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val uuid: String

  /**
   * This is an identifier for the exit, suitable for use in rolling up results (e.g.: "male"), and to display on flowchart canvases.
   * Expressions can reference the name of the exit taken out of another block via @(flow.block_name.exit).
   */
  val name: String

  /**
   * This is the uuid of the Block this exit connects to. It can be null if the exit does not connect to a block (if it is the final block).
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val destination_block: String?

  /**
   * A user-controlled field that can be used to code the meaning of the data collected by this block in a standard taxonomy
   * or coding system, e.g.: a FHIR ValueSet, an industry-specific coding system like SNOMED CT,
   * or an organization's internal taxonomy service. (e.g. "SNOMEDCT::Feminine Gender")
   */
  val semantic_label: String?

  /**
   * For blocks that evaluate conditions, this is an expression that determines whether this exit will be selected
   * as the path out of the block. The first exit with an expression that evaluates to a "truthy" value will be chosen.
   */
  val test: String?

  /**
   * This contains additional information required for each mode supported by the block. Details are provided within the Block documentation
   */
  val config: JsonObject

  /**
   * If this key is present and true, the exit is treated as the flow-through default in a case evaluation.
   * The block will terminate through this exit if no test expressions in other exits evaluate true..
   */
  // todo: should we rename this to isDefault to capture boolean type?
  // todo: we need to update docs -- they specify "key presence", but I'd prefer us to be more explicit
  val default: Boolean?
}

@Serializable
data class BlockExit(
  override val uuid: String,
  override val name: String,
  override val destination_block: String?,
  override val semantic_label: String?,
  override val test: String?,
  override val config: JsonObject,
  override val default: Boolean?
) : IBlockExit