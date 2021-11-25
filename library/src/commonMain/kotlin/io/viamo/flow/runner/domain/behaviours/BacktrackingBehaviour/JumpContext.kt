package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

import io.viamo.flow.runner.flowspec.IBlockInteraction

data class JumpContext(
  val discardedInteractions: List<IBlockInteraction>,
  val destinationInteraction: IBlockInteraction,
)