package io.viamo.flow.runner.model.block

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
