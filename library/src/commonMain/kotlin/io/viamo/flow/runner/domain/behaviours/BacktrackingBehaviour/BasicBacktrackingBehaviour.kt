package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

import ValidationException
import io.viamo.flow.runner.collections.pop
import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.IFlowNavigator
import io.viamo.flow.runner.domain.IPromptBuilder
import io.viamo.flow.runner.domain.NON_INTERACTIVE_BLOCK_TYPES
import io.viamo.flow.runner.domain.behaviours.IBehaviour
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.flowspec.BlockInteraction
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction

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
  suspend fun jumpTo(context: Context, interaction: BlockInteraction): Cursor

  /**
   * Regenerates prompt from previous interaction
   * @param steps
   */
  suspend fun peek(context: Context, steps: Int = 0, direction: PeekDirection = PeekDirection.LEFT): Cursor

  /**
   * Regenerates prompt + interaction in place of previous interaction; updates {@link io.viamo.flow.runner."flow-spec".IContext.cursor}
   * @param steps
   */
  suspend fun seek(context: Context, steps: Int = 0): Cursor
}

/**
 * Basic implementation of time-travel. Solely provides ability to preview what's happened in the past, while any
 * modifications will clear the past's future.
 */
data class BasicBacktrackingBehaviour(
  override val navigator: IFlowNavigator,
  override val promptBuilder: IPromptBuilder
) : IBasicBackTrackingBehaviour {

  var jumpContext: JumpContext? = null

  override fun rebuildIndex(): Unit {
    // do nothing for now
  }

  override suspend fun jumpTo(context: Context, interaction: BlockInteraction): Cursor.RichCursor {
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

    jumpContext = JumpContext(discardedBlockInteractions, interaction)
    val richCursor = navigator.navigateTo(context, destinationBlock)
    jumpContext = null

    return richCursor
  }

  override suspend fun peek(context: Context, steps: Int, direction: PeekDirection): Cursor.RichCursor {
    // keep a trace of all interactions we attempt to make a prompt from
    var localSteps = steps
    var prompt: BasePrompt<*>? = null

    // we'll keep trying to backtrack to an interactive prompt until we run out
    // of interactions -- when that happens, we should catch an exception
    while (prompt == null) {
      try {
        // attempt to build a prompt from the next interaction
        val intx = _findInteractiveInteractionAt(context, localSteps, direction)
        val block = context.findFlowWith(intx.flow_id).findBlockWith(intx.block_id)
        prompt = promptBuilder.buildPromptFor(context, block, intx)

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

  override suspend fun seek(context: Context, steps: Int): Cursor {
    // then generate a cursor from desired interaction && set cursor on context
    return jumpTo(context, peek(context, steps).findInteraction(context))
  }

  override fun postInteractionCreate(context: Context, interaction: BlockInteraction): BlockInteraction {
    return jumpContext?.let { it ->
      interaction.value = it.destinationInteraction.value
      interaction
    } ?: interaction
  }

  override fun postInteractionComplete(context: Context, interaction: IBlockInteraction) {
  }

  private fun _findInteractiveInteractionAt(
    context: Context,
    steps: Int = 0,
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
