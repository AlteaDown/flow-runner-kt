package io.viamo.flow.runner.model.block

interface INumericBlockConfig : IBlockConfig {
  val prompt: String
  val validation_minimum: Double?
  val validation_maximum: Double?
  val ivr: INumericBlockConfigIvr?
}

interface INumericBlockConfigIvr {
  /**
   * @minimum 0
   */
  val max_digits: Number?
}