package io.viamo.flow.runner.block.select_one

import io.viamo.flow.runner.flowspec.IBlock

// todo: currently we don't perform any other behaviour than test evaluation on SelectOne
interface ISelectOneResponseBlock : IBlock {
  override val config: ISelectOneResponseBlockConfig
}
