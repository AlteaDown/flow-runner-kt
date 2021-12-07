package io.viamo.flow.runner.flowspec.block.type.location

import io.viamo.flow.runner.flowspec.block.IBlockConfig

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
