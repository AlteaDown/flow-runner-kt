package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.flowspec.IRichCursorInputRequired

/**
 * Primary interface for interacting with an {@link io.viamo.flow.runner."flow-spec".IContact}; typically not immplemented fully, it is recommended that
 * additional {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations rather extend provided {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt}.
 */
interface IPrompt {
  val interactionId: String
  val config: Any
  val runner: IFlowRunner

  val block: IBlock?
  val value: Any?

  /** Error populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} assignment raises  */
  val error: PromptValidationException?

  /** State populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} is assigned */
  fun isValid(): Boolean

  /** @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.validate} */
  fun validate(value: Any?): Boolean

  /** @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.fulfill} */
  suspend fun fulfill(value: Any): IRichCursorInputRequired?
}

/** Interface for configuration to resolve and build a {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} instance. */
interface IPromptConfig<VALUE> : IBasePromptConfig {
  val kind: String
  val isResponseRequired: Boolean
  val prompt: String
  var value: VALUE?
}

/** Interface for local {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} properties not intersecting with {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} */
interface IBasePromptConfig {
  var isSubmitted: Boolean?
}
