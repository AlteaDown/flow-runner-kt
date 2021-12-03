package io.viamo.flow.runner.block.type.run_flow

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface IRunFlowBlockConfig : IBlockConfigContactEditable {
  val flow_id: String
}
