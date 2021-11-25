package io.viamo.flow.runner.domain.behaviours

import io.viamo.flow.runner.domain.IFlowNavigator
import io.viamo.flow.runner.domain.IPromptBuilder
import io.viamo.flow.runner.flowspec.BlockInteraction
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.IContext

interface IBehaviourConstructor {
  val name: String
  val new: (context: IContext, navigator: IFlowNavigator, promptBuilder: IPromptBuilder) -> IBehaviour

  fun getNameAsKey() = name.removeSuffix("Behaviour").removeSuffix("Behavior").replaceFirstChar { it.lowercase() }
}

data class BehaviourConstructor(
  override val name: String,
  override val new: (context: IContext, navigator: IFlowNavigator, promptBuilder: IPromptBuilder) -> IBehaviour,
) : IBehaviourConstructor

/**
 * Inteface for {@link io.viamo.flow.runner.domain.FlowRunner} extensibility; provides hooks into core runner behaviour.
 */
interface IBehaviour {
  val context: IContext
  val navigator: IFlowNavigator
  val promptBuilder: IPromptBuilder

  /**
   * {@link io.viamo.flow.runner.domain.FlowRunner} hook:
   * - invoked immediately after any block interaction has begun.
   * - invoked immediately before (a) the {@link io.viamo.flow.runner.domain.runners.IBlockRunner} has been initialized (b) the interaction has been pushed
   *   onto the interaction history stack.
   * - also provides an opportunity to generate a different interaction entity; please be wary of this component of
   *   {@link postInteractionCreate()}, this is a very low-level feature and rarely needed, precautions must be taken.
   * @param interaction
   * @param context
   */
  fun postInteractionCreate(interaction: IBlockInteraction): BlockInteraction

  /**
   * {@link io.viamo.flow.runner.domain.FlowRunner} hook:
   * - invoked immediately after (a) the {@link io.viamo.flow.runner.domain.runners.IBlockRunner} has been run (b) the {@link io.viamo.flow.runner."flow-spec".IBlockExit} has been selected
   *   (c) the associated {@link io.viamo.flow.runner.domain.prompt.IPrompt} is marked as {@link io.viamo.flow.runner.domain.prompt.IBasePromptConfig.isSubmitted}.
   * - invoked immediately before the next block is to be discovered.
   * @param interaction
   * @param context
   */
  fun postInteractionComplete(interaction: IBlockInteraction): Unit
}
