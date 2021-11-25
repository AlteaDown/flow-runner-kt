package io.viamo.flow.runner.model.block

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
