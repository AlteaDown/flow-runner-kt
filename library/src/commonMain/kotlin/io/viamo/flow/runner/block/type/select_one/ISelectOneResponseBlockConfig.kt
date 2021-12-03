package io.viamo.flow.runner.block.type.select_one

import io.viamo.flow.runner.block.IBlockConfigContactEditable

interface ISelectOneResponseBlockConfig : IBlockConfigContactEditable {
  val prompt: String
  val question_prompt: String?
  val choices: Map<String, String>
}
