package io.viamo.flow.runner.flowspec.block.type.run_flow

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface IRunFlowBlockConfig : IBlockConfigContactEditable {
  val flow_id: String
}
