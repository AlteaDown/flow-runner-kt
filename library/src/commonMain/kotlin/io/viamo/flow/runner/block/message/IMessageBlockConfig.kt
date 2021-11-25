package io.viamo.flow.runner.block.message

import io.viamo.flow.runner.block.IBlockConfig

interface IMessageBlockConfig : IBlockConfig {
  val prompt: String
}
