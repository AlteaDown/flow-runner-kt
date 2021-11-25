package io.viamo.flow.runner.block.advanced_select_one

import io.viamo.flow.runner.block.IBlockConfig

interface IAdvancedSelectOneBlockConfig : IBlockConfig {
  val prompt: String
  val primary_field: String
  val secondary_fields: List<String>
  val choice_row_fields: List<String>
  val choice_rows: String
  val response_fields: List<String>?
}
