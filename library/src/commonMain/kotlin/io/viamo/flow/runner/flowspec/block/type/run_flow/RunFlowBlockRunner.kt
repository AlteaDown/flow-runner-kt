package io.viamo.flow.runner.flowspec.block.type.run_flow

import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit

/**
 * Block runner for "Core.RunFlow" - This block starts and runs another {@link io.viamo.flow.runner."flow-spec".IFlow}, and returns execution to the
 * current {@link io.viamo.flow.runner."flow-spec".IFlow} when finished.
 *
 * Entry:
 * On entry to this block, control proceeds into the other Flow given by flow_id. The io.viamo.flow.runner."flow-spec".Context for the outer flow is
 * saved and stored within the new inner Flow's io.viamo.flow.runner."flow-spec".Context under the parentFlowContext key.
 *
 * Exit:
 * Multiple levels of nested Flows shall be supported. When an inner Flow terminates, this block resumes execution in
 * the outer Flow. The io.viamo.flow.runner."flow-spec".Context for the inner flow is saved and stored under the childFlowContext key, and flow proceeds
 * through the next block. If an exception exit is triggered within an inner flow causing the inner flow to terminate,
 * flow proceeds through the error exit.
 */
class RunFlowBlockRunner(
  override val block: IRunFlowBlock,
  override val context: Context
) : IBlockRunner<Nothing?> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null

  override suspend fun run(cursor: Cursor): IBlockExit {
    return try {
      block.setContactProperty(context)
      block.firstTrueOrNullBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      block.findDefaultBlockExitOrThrow()
    }
  }
}
