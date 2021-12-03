package io.viamo.flow.runner.domain

import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IContext
import io.viamo.flow.runner.flowspec.IRichCursor
import io.viamo.flow.runner.flowspec.IRichCursorInputRequired

typealias TBlockRunnerFactory = (block: IBlock, ctx: Context) -> IBlockRunner<*>

interface IFlowRunner {
  val context: IContext
  val runnerFactoryStore: Map<String, TBlockRunnerFactory>

  suspend fun initialize(): IRichCursor?

  suspend fun run(): IRichCursorInputRequired?

  fun applyReversibleDataOperation(forward: Any, reverse: Any)
}
