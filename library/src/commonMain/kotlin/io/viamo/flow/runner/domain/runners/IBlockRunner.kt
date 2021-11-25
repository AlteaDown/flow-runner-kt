package io.viamo.flow.runner.domain.runners

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IBlockConfig

/**
 * Interface for running a new block type.
 *
 * There are two methods to implement this contract:
 *
 * - {@link initialize} — Converts an interaction and its block property into either a prompt configuration or
 *   "null".
 *       - {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} is the guts of a prompt and has all of the pieces needed to interact with an
 *         {@link io.viamo.flow.runner."flow-spec".IContact}. If a block type has no need to halt flow execution to interact with the {@link io.viamo.flow.runner."flow-spec".IContact},
 *         then simply returning without any configuration is all we need.
 *       - Some applications will provide the ability to step back through interaction history to a previous point in
 *         time. In this case, we utilize the interaction reference in order to initialize a prompt with the previous
 *         value already pre-populated onto it. This is best practice, and we'll see an example of it below.
 * - {@link run} — Takes the current point in our interaction history and performs some local logic to decide how the
 *   Flow should continue by returning the desired {@link io.viamo.flow.runner."flow-spec".IBlockExit} to be used. In some cases we always resolve to a
 *   single exit, but many cases have more complexity around this part of the puzzle.
 */
interface IBlockRunner<VALUE, BLOCK_CONFIG, PROMPT_CONFIG>
    where BLOCK_CONFIG : IBlockConfig,
          PROMPT_CONFIG : IPromptConfig {

  val block: IBlock<BLOCK_CONFIG>
  val context: IContext

  /**
   * Converts an interaction and its block property into either a prompt configuration or "null".
   * @param interaction
   */
  suspend fun initialize(interaction: IBlockInteraction): IPromptConfig?

  /**
   * Takes the current point in our interaction history and performs some local logic to decide how the Flow should
   * continue by returning the desired {@link io.viamo.flow.runner."flow-spec".IBlockExit} to be used. In some cases we always resolve to a single exit,
   * but many cases have more complexity around this part of the puzzle.
   * @param cursor
   */
  suspend fun run(cursor: IRichCursor<VALUE, BLOCK_CONFIG, PROMPT_CONFIG>): IBlockExit
}