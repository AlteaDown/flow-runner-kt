package io.viamo.flow.runner.flowspec

interface IReadBlockInteractionDetails : IBlockInteractionDetails {
  val read_error: IReadError
}

interface IReadError {
  val message: String
}
