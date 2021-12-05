import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.block.type.message.MESSAGE_PROMPT_KEY
import io.viamo.flow.runner.flowspec.block.type.message.MessagePrompt
import io.viamo.flow.runner.flowspec.block.type.message.MessagePromptConfig
import io.viamo.flow.runner.flowspec.block.type.numeric.NUMERIC_PROMPT_KEY
import io.viamo.flow.runner.flowspec.block.type.numeric.NumericPrompt
import io.viamo.flow.runner.flowspec.block.type.numeric.NumericPromptConfig
import io.viamo.flow.runner.flowspec.block.type.open.OPEN_PROMPT_KEY
import io.viamo.flow.runner.flowspec.block.type.select_many.SelectManyPrompt
import io.viamo.flow.runner.flowspec.block.type.select_many.SelectManyPromptConfig
import io.viamo.flow.runner.flowspec.block.type.select_one.SELECT_ONE_PROMPT_KEY
import io.viamo.flow.runner.flowspec.block.type.select_one.SelectOnePrompt
import io.viamo.flow.runner.flowspec.block.type.select_one.SelectOnePromptConfig

interface IPromptFactory<T> where T : IPromptConfig<T> {
  fun create()
}

fun interface PromptConstructor<VALUE_TYPE> {
  fun new(config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner): BasePrompt<VALUE_TYPE>
}

/**
 * This is a custom Dynamic Enum for Prompts, that allows adding of custom values at runtime, by calling
 * "Prompt.addCustomPrompt()".
 */
@Suppress("MemberVisibilityCanBePrivate")
class Prompt<VALUE_TYPE>(
  val promptConstructor: PromptConstructor<VALUE_TYPE>,
  val promptKey: String,
) {

  companion object {
    private var VALUES: List<Prompt<*>> = listOf()

    val MESSAGE = Prompt(
      { config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        MessagePrompt(config as MessagePromptConfig, interactionId, runner)
      },
      MESSAGE_PROMPT_KEY
    )

    val NUMERIC = Prompt(
      { config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        NumericPrompt(config as NumericPromptConfig, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val SELECT_ONE = Prompt(
      { config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(config as SelectOnePromptConfig, interactionId, runner)
      },
      SELECT_ONE_PROMPT_KEY
    )

    val SELECT_MANY = Prompt(
      { config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectManyPrompt(config as SelectManyPromptConfig, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val OPEN = Prompt(
      { config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(config as SelectOnePromptConfig, interactionId, runner)
      },
      OPEN_PROMPT_KEY
    )

    fun <VALUE_TYPE> addCustomPrompt(promptConstructor: PromptConstructor<VALUE_TYPE>, promptKey: String) {
      Prompt(promptConstructor, promptKey)
    }

    /** Remove custom prompts from the Enum Class */
    fun reset() {
      VALUES = listOf(MESSAGE, NUMERIC, SELECT_ONE, SELECT_MANY, OPEN)
    }

    /**
     * Get a prompt, by the key
     * @param promptKey you can pass io.viamo.flow.runner.domain.prompt.IPromptConfig.kind
     */
    fun valueOf(promptKey: String) = VALUES.firstOrNull { prompt -> prompt.promptKey == promptKey }

    fun values() = VALUES
  }

  /**
   * Construct a prompt, and supply the Prompt constructor for easy instantiation.
   *
   * We do not want the library user of io.viamo.flow.runner.domain.FlowRunner to call this, so io.viamo.flow.runner.domain.FlowRunner should add all custom Prompts via a
   * builder pattern.
   */
  init {
    if (VALUES.any { prompt -> prompt.promptKey == promptKey }) {
      println("Attempted to add duplicate promptKey")
    } else {
      VALUES = VALUES + this
    }
  }
}
