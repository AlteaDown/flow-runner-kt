package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.IRichCursorInputRequired
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.flowspec.block.IBlock

/**
 * Abstract implementation of {@link io.viamo.flow.runner.domain.prompt.IPrompt}, intended to be consumed as a common parent for concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt}
 * implementations.
 */
interface BasePrompt<VALUE> {
  val config: IPromptConfig<VALUE>
  val interactionId: String
  val runner: IFlowRunner
  val key: String
  var value: VALUE?
    get() = config.value as VALUE
    set(value) {
      try {
        this.validate(value)
      } catch (e: PromptValidationException) {
        this.error = e
      }

      this.config.value = value
    }

  /** Error populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} assignment raises  */
  var error: PromptValidationException?

  /**
   * Whether or not a value has been set on this instance.
   * TODO:STOPSHIP We need to make a field that tracks whether the field is set, as not all platforms have null
   **/
  fun isEmpty() = value == null

  val block
    get(): IBlock? {
      return try {
        runner.context.findFlowWith(runner.context.findInteractionWith(interactionId).flow_id)
          .findBlockWith(uuid = runner.context.findInteractionWith(interactionId).block_id)
      } catch (e: ValidationException) {
        e.printStackTrace()
        throw e
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }

  /** @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.fulfill} */
  suspend fun fulfill(value: VALUE?): IRichCursorInputRequired? {
    // allow prompt.fulfill() for continuation
    value?.let { this.value = value }

    return this.runner.run()
  }

  /** State populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} is assigned */
  fun isValid(): Boolean {
    return try {
      validate(config.value as VALUE)
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Template method to be implemented by concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
   * @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.validate
   * @param val
   */
  fun validate(value: Any?): Boolean
}
