package io.viamo.flow.runner.block.selectcase

import io.viamo.flow.runner.flowspec.IBlock
import io.viamo.flow.runner.flowspec.IBlockExit

interface ICaseBlock : IBlock {
  override val config: ICaseBlockConfig
  override val exits: List<IBlockExit>
}
