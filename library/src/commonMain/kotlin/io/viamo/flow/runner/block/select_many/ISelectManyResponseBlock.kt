package io.viamo.flow.runner.block.select_many

import io.viamo.flow.runner.flowspec.IBlock

interface ISelectManyResponseBlock : IBlock {
  override val config: ISelectManyResponseBlockConfig
}
