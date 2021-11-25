package io.viamo.flow.runner.block.set_contact_property

import io.viamo.flow.runner.flowspec.IBlock

interface ISetContactPropertyBlock : IBlock {
  override val config: ISetContactPropertyBlockConfig
}
