package io.viamo.flow.runner.block.select_one

import io.viamo.flow.runner.block.IBlockConfig

interface ISelectOneResponseBlockConfig : IBlockConfig {
  val prompt: String
  val question_prompt: String?
  val choices: Map<String, String>
}

@Deprecated("", ReplaceWith("Map<String, String>"))
typealias StringMapType = Map<String, String>
