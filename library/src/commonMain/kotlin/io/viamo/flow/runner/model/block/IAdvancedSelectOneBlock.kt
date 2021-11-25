package io.viamo.flow.runner.model.block

import io.viamo.flow.runner.flowspec.IBlock

val ADVANCED_SELECT_ONE_BLOCK_TYPE = "MobilePrimitives.Extended.AdvancedSelectOne"

interface IAdvancedSelectOneBlock : IBlock<IAdvancedSelectOneBlockConfig> {
  override val config: IAdvancedSelectOneBlockConfig
}
