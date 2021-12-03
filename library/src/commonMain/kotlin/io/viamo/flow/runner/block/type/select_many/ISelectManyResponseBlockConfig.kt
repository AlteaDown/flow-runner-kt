package io.viamo.flow.runner.block.type.select_many

import io.viamo.flow.runner.block.type.select_one.ISelectOneResponseBlockConfig

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
