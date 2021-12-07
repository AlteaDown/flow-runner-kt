package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.block.IBlock
import kotlinx.serialization.Transient

/**
 * Abstract implementation of {@link io.viamo.flow.runner.domain.prompt.IPrompt}, intended to be consumed as a common parent for concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt}
 * implementations.
 */
interface IBasePrompt<VALUE> {
  val config: IPromptConfig<VALUE>
  val interactionId: String
  val key: String
  var value: VALUE?

  @Transient
  val context: Context

  @Transient
  val runner: IFlowRunner

  /** Error populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} assignment raises  */
  var error: PromptValidationException?

  /**
   * Whether or not a value has been set on this instance.
   * TODO:STOPSHIP We need to make a field that tracks whether the field is set, as not all platforms have null
   **/
  fun isEmpty() = value == null

  val block
    get(): IBlock? = try {
      context.findFlowWith(context.findInteractionWith(interactionId).flow_id)
        .findBlockWith(uuid = context.findInteractionWith(interactionId).block_id)
    } catch (e: ValidationException) {
      e.printStackTrace()
      throw e
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }

  /** State populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} is assigned */
  fun isValid(): Boolean {
    return try {
      validate(config.value)
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Template method to be implemented by concrete {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
   */
  fun validate(value: Any?): Boolean
}

abstract class BasePrompt<VALUE> : IBasePrompt<VALUE> {
  override var value: VALUE? = null
    get() = config.value
    set(value) {
      try {
        validate(value)
      } catch (e: PromptValidationException) {
        e.printStackTrace()
        error = e
      }

      config.value = value
      field = value
    }

  suspend fun fulfill(value: VALUE?): Cursor? {
    // allow prompt.fulfill() for continuation
    value?.let {
      this.value = value
    }

    return runner.run(this.context)
  }
}
