package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.flowspec.*

typealias TGenericPrompt = IPrompt<IPromptConfig<*>, *, *>

/**
 * Abstract implementation of {@link io.viamo.flow.runner.domain.prompt.IPrompt}, intended to be consumed as a common parent for concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt}
 * implementations.
 */
abstract class BasePrompt<VALUE, PROMPT_CONFIG : IPromptConfig<VALUE>>(
  open val config: PROMPT_CONFIG,
  open val interactionId: String,
  open val runner: IFlowRunner,
  open var error: PromptValidationException? = null,
) {
  abstract val key: String

  var value: VALUE?
    get() = config.value
    set(value) {
      try {
        this.validate(value)
      } catch (e: PromptValidationException) {
        this.error = e
      }

      this.config.value = value
    }

  /**
   * Whether or not a value has been set on this instance.
   * TODO:STOPSHIP We need to make a field that tracks whether the field is set, as not all platforms have null
   **/
  fun isEmpty() = this.value == null

  val block
    get(): IBlock<*>? {
      return try {
        findBlockWith(
          uuid = runner.context.findInteractionWith(interactionId).block_id,
          flow = runner.context.findFlowWith(runner.context.findInteractionWith(interactionId).flow_id)
        )
      } catch (e: ValidationException) {
        e.printStackTrace()
        throw e
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }

  suspend fun fulfill(value: VALUE?): IRichCursorInputRequired<*, *, *>? {
    // allow prompt.fulfill() for continuation
    value?.let { this.value = value }

    return this.runner.run()
  }

  fun isValid(): Boolean {
    return try {
      this.validate(this.config.value)
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Template method to be implemented by concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
   * @param val
   */
  abstract fun validate(value: VALUE?): Boolean
}
