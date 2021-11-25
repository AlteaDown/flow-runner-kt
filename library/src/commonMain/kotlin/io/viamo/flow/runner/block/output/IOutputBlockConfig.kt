package io.viamo.flow.runner.block.output

import io.viamo.flow.runner.block.IBlockConfig

interface IOutputBlockConfig : IBlockConfig {
  val value: String
}
