package io.viamo.flow.runner.block.message

import io.viamo.flow.runner.flowspec.IBlock

interface IMessageBlock : IBlock {
  override val config: IMessageBlockConfig
}
