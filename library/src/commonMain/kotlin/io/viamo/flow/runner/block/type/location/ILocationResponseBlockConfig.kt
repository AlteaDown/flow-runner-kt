package io.viamo.flow.runner.block.type.location

import io.viamo.flow.runner.block.IBlockConfig

interface ILocationResponseBlockConfig : IBlockConfig {

  val prompt: String

  /**
   * @minimum 0
   */
  val accuracy_threshold_meters: Number

  /**
   * @minimum 0
   */
  val accuracy_timeout_seconds: Number
}
