package io.viamo.flow.runner.flowspec

import ValidationException
import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.domain.IIdGenerator
import io.viamo.flow.runner.domain.createFormattedDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface IBlockInteraction {
  // UUID64
  val uuid: String

  // UUID32
  val block_id: String

  // UUID32
  val flow_id: String
  val entry_at: String
  var exit_at: String?
  var has_response: Boolean
  var value: String?
  val details: Map<String, JsonElement>
  var selected_exit_id: String?
  val type: String

  // UUID64
  val origin_block_interaction_id: String?

  // UUID64
  val origin_flow_id: String?
}

@Serializable
data class BlockInteraction(
  // UUID64
  override val uuid: String,

  // UUID32
  override val block_id: String,

  // UUID32
  override val flow_id: String,
  override val entry_at: String,
  override var exit_at: String?,
  override var has_response: Boolean,
  override var value: String?,
  override val details: Map<String, JsonElement>,
  override var selected_exit_id: String?,
  override val type: String,

  // UUID64
  override val origin_block_interaction_id: String?,

  // UUID64
  override val origin_flow_id: String?,
) : IBlockInteraction {

  /**
   * Find next block leveraging destinationBlock on current interaction's "selectedExit".
   * Raises when "selectedExitId" absent.
   * @param block_id
   * @param selectedExitId
   * @param ctx
   */
  fun findNextBlockFrom(context: Context): IBlock {
    return selected_exit_id?.let { selected_exit_id: String ->
      val block = context.findBlockOnActiveFlowWith(block_id)
      val destinationBlock = block.findBlockExitWith(selected_exit_id).destination_block
      (context.getActiveFlow()).blocks.find { it.uuid == destinationBlock }
    } ?: throw ValidationException("Unable to navigate past incomplete interaction; did you forget to call runner.run()?")
  }

  companion object {
    /**
     * Generate a concrete "io.viamo.flow.runner."flow-spec".IBlockInteraction" data object, pre-populated with:
     * - UUID via "io.viamo.flow.runner.domain.IIdGenerator.generate()"
     * - entryAt via current timestamp
     * - flowId (provisioned)
     * - block_id via block.uuid
     * - type via block.type provisioned
     * - hasResponse as "false"
     * @param block_id
     * @param type
     * @param flowId
     * @param originFlowId
     * @param originBlockInteractionId
     */
    suspend fun createBlockInteractionFor(
      block: IBlock,
      flowId: String,
      originFlowId: String?,
      originBlockInteractionId: String?,
      idGenerator: IIdGenerator,
    ): IBlockInteraction {
      return BlockInteraction(
        uuid = idGenerator.generate(),
        block_id = block.uuid,
        flow_id = flowId,
        entry_at = createFormattedDate(),
        exit_at = null,
        has_response = false,
        value = null,
        selected_exit_id = null,
        origin_flow_id = originFlowId,
        origin_block_interaction_id = originBlockInteractionId,
        type = block.getType() ?: error("Type was not set"),
        details = mapOf()
      )
    }
  }
}

interface IBlockInteractionDetails
