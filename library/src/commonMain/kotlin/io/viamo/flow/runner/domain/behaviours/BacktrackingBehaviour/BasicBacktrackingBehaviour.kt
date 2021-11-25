package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

import ValidationException
import io.viamo.flow.runner.collections.pop
import io.viamo.flow.runner.domain.IFlowNavigator
import io.viamo.flow.runner.domain.IPromptBuilder
import io.viamo.flow.runner.domain.NON_INTERACTIVE_BLOCK_TYPES
import io.viamo.flow.runner.domain.behaviours.IBehaviour
import io.viamo.flow.runner.domain.prompt.IPrompt
import io.viamo.flow.runner.flowspec.*

enum class PeekDirection {
  RIGHT,
  LEFT,
}

/**
 * Interface for time-travel within interaction history.
 */
interface IBasicBackTrackingBehaviour : IBehaviour {
  /**
   * Rebuild index over interaction history from scratch.
   */
  fun rebuildIndex()

  /**
   * Generates new prompt from new interaction + resets state to what was {@link io.viamo.flow.runner."flow-spec".IContext.interactions}'s moment
   * @param interaction
   * todo: this should likely take in steps rather than interaction itself */
  fun jumpTo(interaction: IBlockInteraction): IRichCursorInputRequired<*, *, *>

  /**
   * Regenerates prompt from previous interaction
   * @param steps
   */
  suspend fun peek(steps: Int = 0, direction: PeekDirection = PeekDirection.LEFT): IRichCursorInputRequired<*, *, *>

  /**
   * Regenerates prompt + interaction in place of previous interaction; updates {@link io.viamo.flow.runner."flow-spec".IContext.cursor}
   * @param steps
   */
  suspend fun seek(steps: Int = 0): IRichCursorInputRequired<*, *, *>
}

/**
 * Basic implementation of time-travel. Solely provides ability to preview what's happened in the past, while any
 * modifications will clear the past's future.
 */
data class BasicBacktrackingBehaviour(
  override val context: IContext,
  override val navigator: IFlowNavigator,
  override val promptBuilder: IPromptBuilder
) : IBasicBackTrackingBehaviour {

  var jumpContext: JumpContext? = null

  override fun rebuildIndex(): Unit {
    // do nothing for now
  }

  override fun jumpTo(interaction: IBlockInteraction): IRichCursorInputRequired<*, *, *> {
    // jump context.interactions back in time
    val discardedBlockInteractions = context.interactions.subList(
      // truncate intx list to pull us back in time; include provided intx
      context.interactions.lastIndexOf(interaction),
      context.interactions.size
    )

    // step out of nested flows that we've truncated
    // todo: migrate to also use applyReversibleDataOperation()
    discardedBlockInteractions.reversed().forEach { intx ->
      if (intx.uuid == context.nested_flow_block_interaction_id_stack.last()) {
        context.nested_flow_block_interaction_id_stack.pop()
      }
    }

    // can only reverse from the end, so we only compare the last.
    discardedBlockInteractions.reversed().forEach {
      while (context.reversible_operations.lastOrNull()?.interactionId == it.uuid) {
        TODO("was reverseLastDataOperation(context)")
      }
    }

    val destinationBlock = context.findBlockOnActiveFlowWith(interaction.block_id)

    this.jumpContext = JumpContext(discardedBlockInteractions, interaction)
    val richCursor = this.navigator.navigateTo(destinationBlock, context)
    this.jumpContext = null

    return richCursor
  }

  override suspend fun peek(steps: Int, direction: PeekDirection): IRichCursorInputRequired<*, *, *> {
    // keep a trace of all interactions we attempt to make a prompt from
    var localSteps = steps
    var prompt: IPrompt<*, *, *>? = null

    // we'll keep trying to backtrack to an interactive prompt until we run out
    // of interactions -- when that happens, we should catch an exception
    while (prompt == null) {
      try {
        // attempt to build a prompt from the next interaction
        val intx = this._findInteractiveInteractionAt(localSteps, context, direction)
        val block = findBlockWith(intx.block_id, context.findFlowWith(intx.flow_id))
        prompt = this.promptBuilder.buildPromptFor(block, intx)

        if (prompt != null) {
          prompt = TODO("Was Object.assign(prompt, { value: intx.value }). Also uncomment below after fixing.")
          // return RichCursorInputRequired(
          //   intx,
          //   TODO("Was Object.assign(prompt, { value: intx.value })"),
          // )
        }
        // we'll try stepping over the interaction that had no prompt
        ++localSteps
        // we weren't able to build a prompt
      } catch (e: Throwable) {
        throw ValidationException("${e.message}:\nSkipped Interactions with No Prompt")
      }
    }
    throw ValidationException("Logic error when backtracking.\nSkipped Interactions with No Prompt")
  }

  override suspend fun seek(steps: Int): IRichCursorInputRequired<*, *, *> {
    val prevIntx = this.peek(steps).interaction
    // then generate a cursor from desired interaction && set cursor on context
    return (this.jumpTo(prevIntx))
  }

  override fun postInteractionCreate(interaction: BlockInteraction): BlockInteraction {
    return this.jumpContext?.let {
      interaction.copy(value = it.destinationInteraction.value)
    } ?: interaction
  }

  override fun postInteractionComplete(interaction: BlockInteraction) {}

  private fun _findInteractiveInteractionAt(
    steps: Int = 0,
    context: IContext = this.context,
    direction: PeekDirection = PeekDirection.LEFT
  ): IBlockInteraction {
    check(direction == PeekDirection.LEFT || direction == PeekDirection.RIGHT) { "direction must be LEFT or RIGHT, but found $direction" }

    var stepsLeft = steps + 1
    return when (direction) {
      PeekDirection.RIGHT -> context.interactions.find { interaction -> !NON_INTERACTIVE_BLOCK_TYPES.contains(interaction.type) && --stepsLeft == 0 }
      PeekDirection.LEFT -> context.interactions.findLast { interaction -> !NON_INTERACTIVE_BLOCK_TYPES.contains(interaction.type) && --stepsLeft == 0 }
    } ?: throw ValidationException("Unable to backtrack to an interaction that far back")
  }
}
