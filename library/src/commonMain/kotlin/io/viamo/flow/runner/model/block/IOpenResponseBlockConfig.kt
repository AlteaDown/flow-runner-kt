@file:Suppress("PropertyName")

package io.viamo.flow.runner.model.block

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