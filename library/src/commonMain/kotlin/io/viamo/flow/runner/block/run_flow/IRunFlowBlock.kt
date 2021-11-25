package io.viamo.flow.runner.block.run_flow

import io.viamo.flow.runner.flowspec.IBlock

interface IRunFlowBlock : IBlock {
  override val config: IRunFlowBlockConfig
}
