package io.viamo.flow.runner.block.photo_response

import io.viamo.flow.runner.block.IBlockConfig

interface IPhotoResponseBlockConfig : IBlockConfig {

  val prompt: String
}
