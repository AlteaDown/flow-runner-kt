package io.viamo.flow.runner.block.type.output

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface IOutputBlockConfig : IBlockConfigContactEditable {
  val value: String
}
