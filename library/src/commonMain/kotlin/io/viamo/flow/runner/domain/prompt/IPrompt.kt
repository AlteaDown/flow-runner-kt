package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.flowspec.IBlock
import io.viamo.flow.runner.flowspec.IRichCursorInputRequired
import io.viamo.flow.runner.model.block.IBlockConfig

/**
 * Primary interface for interacting with an {@link io.viamo.flow.runner."flow-spec".IContact}; typically not immplemented fully, it is recommended that
 * additional {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations rather extend provided {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt}.
 */
interface IPrompt<PROMPT_CONFIG_TYPE : IPromptConfig<VALUE_TYPE>, VALUE_TYPE, BLOCK_CONFIG_TYPE : IBlockConfig> {
  val interactionId: String
  val config: PROMPT_CONFIG_TYPE
  val runner: IFlowRunner

  val block: IBlock<BLOCK_CONFIG_TYPE>?
  val value: VALUE_TYPE?

  /** Error populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} assignment raises  */
  val error: PromptValidationException?

  /** State populated when {@link io.viamo.flow.runner.domain.prompt.IPrompt.value} is assigned */
  fun isValid(): Boolean

  /** @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.validate} */
  fun validate(value: Any): Boolean

  /** @see {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt.fulfill} */
  suspend fun fulfill(value: Any): IRichCursorInputRequired<PROMPT_CONFIG_TYPE, VALUE_TYPE, BLOCK_CONFIG_TYPE>?
}

/** Interface for configuration to resolve and build a {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} instance. */
interface IPromptConfig<VALUE_TYPE> : IBasePromptConfig {
  val kind: String
  val isResponseRequired: Boolean
  val prompt: String
  var value: VALUE_TYPE?
}

interface INoPromptConfig : IPromptConfig<Nothing?> {
  override val kind: String
  override val isResponseRequired: Boolean
  override val prompt: String
  override var value: Nothing?
}

/** Interface for local {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} properties not intersecting with {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} */
interface IBasePromptConfig {
  var isSubmitted: Boolean?
}
