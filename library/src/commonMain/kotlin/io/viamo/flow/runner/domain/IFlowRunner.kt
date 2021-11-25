package io.viamo.flow.runner.domain

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IBlockConfig

typealias TBlockRunnerFactory = (block: IBlock<*>, ctx: Context) -> IBlockRunner<*, out IBlockConfig, out IPromptConfig<*>>

typealias IBlockRunnerFactoryStore = Map<String, TBlockRunnerFactory>

interface IFlowRunner {
  val context: IContext
  val runnerFactoryStore: Map<String, TBlockRunnerFactory>

  // new (context: io.viamo.flow.runner."flow-spec".IContext): io.viamo.flow.runner.domain.IFlowRunner

  suspend fun initialize(): IRichCursor<*, *, *>?

  suspend fun run(): IRichCursorInputRequired<*, *, *>?

  fun applyReversibleDataOperation(forward: NonBreakingUpdateOperation, reverse: NonBreakingUpdateOperation, context: IContext): Unit
}
