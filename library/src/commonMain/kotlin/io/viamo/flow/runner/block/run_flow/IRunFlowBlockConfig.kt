package io.viamo.flow.runner.block.run_flow

import io.viamo.flow.runner.block.IBlockConfig

interface IRunFlowBlockConfig : IBlockConfig {
  val flow_id: String
}
