package io.viamo.flow.runner.block.type.log

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface ILogBlockConfig : IBlockConfigContactEditable {
  val message: String
}
