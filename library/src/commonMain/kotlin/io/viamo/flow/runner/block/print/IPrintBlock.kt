package io.viamo.flow.runner.block.print

import io.viamo.flow.runner.flowspec.IBlock

interface IPrintBlock : IBlock {
  override val config: IPrintBlockConfig
}
