package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

import io.viamo.flow.runner.flowspec.BlockInteraction

data class JumpContext(
  val discardedInteractions: List<BlockInteraction>,
  val destinationInteraction: BlockInteraction,
)