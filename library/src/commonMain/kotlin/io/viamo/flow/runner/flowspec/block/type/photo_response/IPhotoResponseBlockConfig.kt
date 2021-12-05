package io.viamo.flow.runner.flowspec.block.type.photo_response

import io.viamo.flow.runner.flowspec.block.IBlockConfig

interface IPhotoResponseBlockConfig : IBlockConfig {
  val prompt: String
}
