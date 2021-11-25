package io.viamo.flow.runner.block.output

import io.viamo.flow.runner.flowspec.IBlock

interface IOutputBlock : IBlock {
  override val config: IOutputBlockConfig
}
