package io.viamo.flow.runner.flowspec.block.type.read

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable

interface IReadBlockConfig : IBlockConfigContactEditable {
  /** This is a "scanf"-compatible format String, where any %-characters will be read into context variables. */
  val format_String: String

  /** This is a list of Strings, containing the variable names in the context where the results will be stored.
   *  The number of variable names must match the number of %-characters in format_String. */
  val destination_variables: List<String>
}
