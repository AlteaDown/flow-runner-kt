import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.*

interface IPromptFactory<T> where T : IPromptConfig<*> {
  fun create()
}

fun interface PromptConstructor<VALUE_TYPE, PROMPT_CONFIG_TYPE : IPromptConfig<*>> {
  fun new(config: PROMPT_CONFIG_TYPE, interactionId: String, runner: IFlowRunner): BasePrompt<VALUE_TYPE, *>
}

/**
 * This is a custom Dynamic Enum for Prompts, that allows adding of custom values at runtime, by calling
 * "Prompt.addCustomPrompt()".
 */
class Prompt<VALUE_TYPE>(
  val promptConstructor: PromptConstructor<VALUE_TYPE, out IPromptConfig<VALUE_TYPE>>,
  val promptKey: String,
) {

  companion object {
    private var VALUES: List<Prompt<*>> = listOf()

    val MESSAGE = Prompt(
      PromptConstructor { config: MessagePromptConfig, interactionId: String, runner: IFlowRunner ->
        MessagePrompt(config, interactionId, runner)
      },
      MESSAGE_PROMPT_KEY
    )

    val NUMERIC = Prompt(
      PromptConstructor { config: NumericPromptConfig, interactionId: String, runner: IFlowRunner ->
        NumericPrompt(config, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val SELECT_ONE = Prompt(
      PromptConstructor { config: SelectOnePromptConfig, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(config, interactionId, runner)
      },
      SELECT_ONE_PROMPT_KEY
    )

    val SELECT_MANY = Prompt(
      PromptConstructor { config: SelectManyPromptConfig, interactionId: String, runner: IFlowRunner ->
        SelectManyPrompt(config, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val OPEN = Prompt(
      PromptConstructor { config: SelectOnePromptConfig, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(config, interactionId, runner)
      },
      OPEN_PROMPT_KEY
    )

    fun <VALUE_TYPE> addCustomPrompt(promptConstructor: PromptConstructor<VALUE_TYPE, IPromptConfig<VALUE_TYPE>>, promptKey: String) {
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
    fun <T> valueOf(promptKey: String) = VALUES.firstOrNull { prompt -> prompt.promptKey == promptKey } as Prompt<T>?

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
