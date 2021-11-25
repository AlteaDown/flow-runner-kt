@file:Suppress("PropertyName")

package io.viamo.flow.runner.block.open

import io.viamo.flow.runner.block.IBlockConfig

interface IOpenResponseBlockConfig : IBlockConfig {
  val prompt: String
  val ivr: IOpenResponseBlockConfigIvr?
  val text: IOpenResponseBlockConfigText?
}

interface IOpenResponseBlockConfigIvr {
  /**
   * @minimum 0
   */
  val max_duration_seconds: Number?
}

interface IOpenResponseBlockConfigText {
  /**
   * @minimum 0
   */
  val max_response_characters: Int?
}