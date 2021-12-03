package io.viamo.flow.runner.block.type.run_flow

import ValidationException
import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.block.IBlockContactEditable
import io.viamo.flow.runner.block.IBlockExit
import io.viamo.flow.runner.block.IBlockUIMetadata
import io.viamo.flow.runner.collections.push
import io.viamo.flow.runner.flowspec.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

interface IRunFlowBlock : IBlockContactEditable {
  override val config: IRunFlowBlockConfig
}

@Serializable
data class RunFlowBlock(
  override val uuid: String,
  override val name: String,
  override val label: String?,
  override val semantic_label: String?,
  override val tags: List<String>?,
  override val vendor_metadata: JsonObject?,
  override val ui_metadata: IBlockUIMetadata,
  override val config: IRunFlowBlockConfig,
  override val exits: List<IBlockExit>
) : IRunFlowBlock {

  /**
   * Stepping into is the act of moving into a child flow.
   * However, we can't move into a child flow without a cursor indicating we've moved.
   * "stepInto()" needs to be the thing that discovers ya from xa (via first on flow in flows list)
   * Then generating a cursor that indicates where we are.
   * ?? -> xa ->>> ya -> yb ->>> xb
   *
   * todo: would it be possible for stepping into and out of be handled by the RunFlow itself?
   *       Eg. these are esentially RunFlowRunner's .start() + .resume() equivalents */
  fun stepIntoAndGetNextBlock(context: Context): IBlock? {
    val runFlowInteraction = context.interactions.lastOrNull()
        ?: throw ValidationException("Unable to step into Core.RunFlow that hasn't yet been started")

    check(uuid == runFlowInteraction.block_id) { "Unable to step into Core.RunFlow block that doesn't match last interaction" }

    context.nested_flow_block_interaction_id_stack.push(runFlowInteraction.uuid)

    // todo: use io.viamo.flow.runner."flow-spec".IFlow.firstBlockId
    return context.getActiveFlow().blocks.firstOrNull()
  }
}