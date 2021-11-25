package io.viamo.flow.runner.flowspec

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface IBlockInteraction {
  // UUID64
  val uuid: String

  // UUID32
  val block_id: String

  // UUID32
  val flow_id: String
  val entry_at: String
  var exit_at: String?
  var has_response: Boolean
  var value: String?
  val details: Map<String, JsonElement>
  var selected_exit_id: String?
  val type: String

  // UUID64
  val origin_block_interaction_id: String?

  // UUID64
  val origin_flow_id: String?
}

@Serializable
data class BlockInteraction(
  // UUID64
  override val uuid: String,

  // UUID32
  override val block_id: String,

  // UUID32
  override val flow_id: String,
  override val entry_at: String,
  override var exit_at: String?,
  override var has_response: Boolean,
  override var value: String?,
  override val details: Map<String, JsonElement>,
  override var selected_exit_id: String?,
  override val type: String,

  // UUID64
  override val origin_block_interaction_id: String?,

  // UUID64
  override val origin_flow_id: String?,
) : IBlockInteraction

interface IBlockInteractionDetails
