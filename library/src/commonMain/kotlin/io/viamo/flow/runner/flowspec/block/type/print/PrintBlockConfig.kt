package io.viamo.flow.runner.flowspec.block.type.print

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface IPrintBlockConfig : IBlockConfigContactEditable {
  val message: String
}
