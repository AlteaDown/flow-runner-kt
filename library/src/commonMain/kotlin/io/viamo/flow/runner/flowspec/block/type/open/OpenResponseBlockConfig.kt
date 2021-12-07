@file:Suppress("PropertyName")

package io.viamo.flow.runner.flowspec.block.type.open

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface IOpenResponseBlockConfig : IBlockConfigContactEditable {
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