package io.viamo.flow.runner.block.print

import io.viamo.flow.runner.block.IBlockConfig

interface IPrintBlockConfig : IBlockConfig {
  val message: String
}
