package io.viamo.flow.runner.block.advanced_select_one

import io.viamo.flow.runner.flowspec.IBlock

const val ADVANCED_SELECT_ONE_BLOCK_TYPE = "MobilePrimitives.Extended.AdvancedSelectOne"

interface IAdvancedSelectOneBlock : IBlock {
  override val config: IAdvancedSelectOneBlockConfig
}
