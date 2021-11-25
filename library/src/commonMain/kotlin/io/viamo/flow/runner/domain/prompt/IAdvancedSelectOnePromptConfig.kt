package io.viamo.flow.runner.domain.prompt

import kotlinx.serialization.Serializable

interface IAdvancedSelectOnePromptConfig : IPromptConfig<List<AdvancedSelectOne>> {
  val primaryField: String
  val secondaryFields: List<String>
  val choiceRowFields: List<String>
  val choiceRows: String
  val responseFields: List<String>?
}

class AdvancedSelectOnePromptConfig(
  override val prompt: String,
  override val primaryField: String,
  override val secondaryFields: List<String>,
  override val choiceRowFields: List<String>,
  override val choiceRows: String,
  override val responseFields: List<String>?,
  override var value: List<AdvancedSelectOne>? = null,
  override var isSubmitted: Boolean? = false,
) : IAdvancedSelectOnePromptConfig {
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
