package io.viamo.flow.runner.block.type.print

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface IPrintBlockConfig : IBlockConfigContactEditable {
  val message: String
}
