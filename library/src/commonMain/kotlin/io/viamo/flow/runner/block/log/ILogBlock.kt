package io.viamo.flow.runner.block.log

import io.viamo.flow.runner.flowspec.IBlock

interface ILogBlock : IBlock {
  override val config: ILogBlockConfig
}
