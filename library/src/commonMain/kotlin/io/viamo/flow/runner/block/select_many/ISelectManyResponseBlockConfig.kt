package io.viamo.flow.runner.block.select_many

import io.viamo.flow.runner.block.select_one.ISelectOneResponseBlockConfig

interface ISelectManyResponseBlockConfig : ISelectOneResponseBlockConfig {
  /**
   * @minimum 1
   */
  val minimum_choices: Number?

  /**
   * @minimum 1
   */
  val maximum_choices: Number?
}
