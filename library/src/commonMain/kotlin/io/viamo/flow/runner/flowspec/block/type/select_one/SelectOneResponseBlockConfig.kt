package io.viamo.flow.runner.flowspec.block.type.select_one

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface ISelectOneResponseBlockConfig : IBlockConfigContactEditable {
  val prompt: String
  val question_prompt: String?
  val choices: Map<String, String>
}
