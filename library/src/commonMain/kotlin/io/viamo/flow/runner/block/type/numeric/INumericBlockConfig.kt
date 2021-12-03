package io.viamo.flow.runner.block.type.numeric

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface INumericBlockConfig : IBlockConfigContactEditable {
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