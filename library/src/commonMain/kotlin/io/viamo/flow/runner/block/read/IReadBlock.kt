package io.viamo.flow.runner.block.read

import io.viamo.flow.runner.flowspec.IBlock

interface IReadBlock : IBlock {
  override val config: IReadBlockConfig
}
