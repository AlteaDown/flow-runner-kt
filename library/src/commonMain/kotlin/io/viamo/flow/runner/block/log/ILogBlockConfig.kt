package io.viamo.flow.runner.block.log

import io.viamo.flow.runner.block.IBlockConfig

interface ILogBlockConfig : IBlockConfig {
  val message: String
}
