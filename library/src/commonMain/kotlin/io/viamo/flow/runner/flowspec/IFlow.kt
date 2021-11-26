package io.viamo.flow.runner.flowspec

/**
 * Flow structure: https://floip.gitbook.io/flow-specification/flows#flows
 */
interface IFlow {
  /**
   * A globally unique identifier for this Flow.  (See UUID Format: https://floip.gitbook.io/flow-specification/flows#uuid-format)
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  // UUID32
  val uuid: String

  /**
   * A human-readable name for the Flow content
   *
   * @minLength 3
   */
  val name: String

  /**
   * An extended user-provided description for the flow.
   */
  val label: String?

  /**
   * The time when this flow was last modified, in UTC, with microsecond precision: "2016-12-25 13:42:05.234598"
   *
   * @format date-time
   */
  // UTC like: 2016-12-25 13:42:05.234598
  val last_modified: String

  /**
   * The number of seconds of inactivity after which io.viamo.flow.runner."flow-spec".Contact input for this flow is no longer accepted, and Runs in progress are terminated
   *
   * @minimum 0
   * @type integer // TODO: this is not working, nor: @type "integer"
   */
  val interaction_timeout: Number

  /**
   * A set of key-value elements that is not controlled by the Specification,
   * but could be relevant to a specific vendor/platform/implementation.
   */
  val vendor_metadata: Any?

  /**
   * A list of the supported Modes that the Flow has content suitable for.
   *
   * @minItems 1
   */
  val supported_modes: List<SupportedMode>

  /**
   * A list of the languages that the Flow has suitable content for.
   * See language object specification: https://floip.gitbook.io/flow-specification/flows#language-objects-and-identifiers.
   *
   * @minItems 1
   */
  val languages: List<ILanguage>

  /**
   * A list of the Blocks in the flow.
   *
   * @minItems 1
   */
  val blocks: List<IBlock>

  /**
   * The ID of the block in blocks that is at the beginning of the flow.
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val first_block_id: String

  /**
   * If provided, the ID of the block in blocks that will be jumped to if there is an error or deliberate exit condition during Flow Run.
   * If not provided, the Flow Run will end immediately.
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val exit_block_id: String?
}

fun findBlockWith(uuid: String, flow: IFlow): IBlock {
  return flow.blocks.firstOrNull { it.uuid == uuid }
      ?: throw IllegalStateException("Unable to find block on flow")
}

interface IFlowService {
  fun findBlockWith(uuid: String, flow: IFlow): IBlock
}
