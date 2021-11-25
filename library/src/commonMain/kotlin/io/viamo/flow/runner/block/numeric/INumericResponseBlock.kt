package io.viamo.flow.runner.block.numeric

import io.viamo.flow.runner.flowspec.IBlock

interface INumericResponseBlock : IBlock {
  override val config: INumericBlockConfig
}
