package io.viamo.flow.runner.block.photo_response

import io.viamo.flow.runner.flowspec.IBlock

interface IPhotoResponseBlock : IBlock {
  override val config: IPhotoResponseBlockConfig
}
