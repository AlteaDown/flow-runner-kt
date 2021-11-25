package io.viamo.flow.runner.block.open

import io.viamo.flow.runner.flowspec.IBlock

interface IOpenResponseBlock : IBlock {
  override val config: IOpenResponseBlockConfig
}
