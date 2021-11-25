package io.viamo.flow.runner.model.block

import io.viamo.flow.runner.flowspec.IBlock
import io.viamo.flow.runner.flowspec.IBlockExit

interface ICaseBlock : IBlock<ICaseBlockConfig> {
  val exits: List<IBlockExit<Any>>
}
