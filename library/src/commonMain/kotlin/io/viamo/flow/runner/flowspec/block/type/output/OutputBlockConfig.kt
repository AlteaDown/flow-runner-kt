package io.viamo.flow.runner.flowspec.block.type.output

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface IOutputBlockConfig : IBlockConfigContactEditable {
  val value: String
}
