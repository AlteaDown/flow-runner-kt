package io.viamo.flow.runner.domain.runners

import io.viamo.flow.runner.domain.prompt.AdvancedSelectOne
import io.viamo.flow.runner.domain.prompt.AdvancedSelectOnePromptConfig
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IAdvancedSelectOneBlock
import io.viamo.flow.runner.model.block.IAdvancedSelectOneBlockConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AdvancedSelectOneBlockRunner(
  override val block: IAdvancedSelectOneBlock,
  override val context: IContext
) : IBlockRunner<List<AdvancedSelectOne>, IAdvancedSelectOneBlockConfig, AdvancedSelectOnePromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): AdvancedSelectOnePromptConfig {
    val prompt = block.config.prompt

    return block.config.run {
      AdvancedSelectOnePromptConfig(
        prompt,
        primary_field,
        secondary_fields,
        choice_row_fields,
        choice_rows,
        response_fields,
        value = interaction.value?.let {
          Json.decodeFromString(it)
        },
        isSubmitted = interaction.has_response,
      )
    }
  }

  override suspend fun run(cursor: IRichCursor<List<AdvancedSelectOne>, IAdvancedSelectOneBlockConfig, AdvancedSelectOnePromptConfig>): IBlockExit {
    return try {
      setContactProperty(block, context)
      firstTrueOrNullBlockExitOrThrow(block, context)
    } catch (e: Exception) {
      println(e)
      findDefaultBlockExitOrThrow(block)
    }
  }
}
