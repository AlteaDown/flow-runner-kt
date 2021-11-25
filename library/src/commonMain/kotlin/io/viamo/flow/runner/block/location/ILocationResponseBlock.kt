package io.viamo.flow.runner.block.location

import io.viamo.flow.runner.flowspec.IBlock

interface ILocationResponseBlock : IBlock {
  override val config: ILocationResponseBlockConfig
}
