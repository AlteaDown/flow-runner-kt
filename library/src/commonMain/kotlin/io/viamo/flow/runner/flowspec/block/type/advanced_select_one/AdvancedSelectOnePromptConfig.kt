package io.viamo.flow.runner.flowspec.block.type.advanced_select_one

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import kotlinx.serialization.Serializable

interface IAdvancedSelectOnePromptConfig : IPromptConfig<List<AdvancedSelectOne>> {
  val primaryField: String
  val secondaryFields: List<String>
  val choiceRowFields: List<String>
  val choiceRows: String
  val responseFields: List<String>?
  override var value: List<AdvancedSelectOne>?
}

@Serializable
class AdvancedSelectOnePromptConfig(
  override val prompt: String,
  override val primaryField: String,
  override val secondaryFields: List<String>,
  override val choiceRowFields: List<String>,
  override val choiceRows: String,
  override val responseFields: List<String>?,
  override var value: List<AdvancedSelectOne>? = null,
  override var isSubmitted: Boolean? = false,
) : IPromptConfig<List<AdvancedSelectOne>>, IAdvancedSelectOnePromptConfig {
  override val kind = ADVANCED_SELECT_ONE_PROMPT_KEY
  override val isResponseRequired: Boolean = true
}

interface IAdvancedSelectOne {
  val name: String
  val value: String
}

@Serializable
data class AdvancedSelectOne(
  override val name: String,
  override val value: String,
) : IAdvancedSelectOne
