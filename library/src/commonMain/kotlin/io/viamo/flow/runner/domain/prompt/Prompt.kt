import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.Context
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
  fun new(context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner): BasePrompt<VALUE_TYPE>
}

/**
 * This is a custom Dynamic Enum for Prompts, that allows adding of custom values at runtime, by calling
 * "Prompt.addCustomPrompt()".
 */
@Suppress("MemberVisibilityCanBePrivate")
class Prompt<VALUE_TYPE>(
  val builder: PromptConstructor<VALUE_TYPE>,
  val key: String,
) {

  companion object {

    val MESSAGE = Prompt(
      { context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        MessagePrompt(context, config as MessagePromptConfig, interactionId, runner)
      },
      MESSAGE_PROMPT_KEY
    )

    val NUMERIC = Prompt(
      { context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        NumericPrompt(context, config as NumericPromptConfig, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val SELECT_ONE = Prompt(
      { context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(context, config as SelectOnePromptConfig, interactionId, runner)
      },
      SELECT_ONE_PROMPT_KEY
    )

    val SELECT_MANY = Prompt(
      { context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectManyPrompt(context, config as SelectManyPromptConfig, interactionId, runner)
      },
      NUMERIC_PROMPT_KEY
    )

    val OPEN = Prompt(
      { context: Context, config: IPromptConfig<*>, interactionId: String, runner: IFlowRunner ->
        SelectOnePrompt(context, config as SelectOnePromptConfig, interactionId, runner)
      },
      OPEN_PROMPT_KEY
    )

    val DEFAULT: List<Prompt<*>> = listOf(MESSAGE, NUMERIC, SELECT_ONE, SELECT_MANY, OPEN)

    fun <VALUE_TYPE> addCustomPrompt(promptConstructor: PromptConstructor<VALUE_TYPE>, promptKey: String) {
      Prompt(promptConstructor, promptKey)
    }
  }
}
