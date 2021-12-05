package io.viamo.flow.runner.flowspec.block.type.log

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface ILogBlockConfig : IBlockConfigContactEditable {
  val message: String
}
