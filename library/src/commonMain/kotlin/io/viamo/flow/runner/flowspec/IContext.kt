package io.viamo.flow.runner.flowspec

import ValidationException
import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.block.type.run_flow.IRunFlowBlockConfig
import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.flowspec.contact.IContact
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface IReversibleUpdateOperation {
  val interactionId: String?
  val forward:/* TODO Was NonBreakingUpdateOperation */ Any
  val reverse: /* TODO Was NonBreakingUpdateOperation */ Any
}

interface IContext {
  val id: String
  val created_at: String
  val entry_at: String?
  var exit_at: String?
  var delivery_status: DeliveryStatus
  val user_id: String?
  val org_id: String?
  val mode: SupportedMode
  val language_id: String
  val contact: IContact
  val groups: List<IGroup>
  val session_vars: MutableMap<String, JsonElement>
  val interactions: List<IBlockInteraction>
  val nested_flow_block_interaction_id_stack: Stack<String>
  val reversible_operations: List<IReversibleUpdateOperation>
  val cursor: ICursor?
  val flows: List<IFlow>
  val first_flow_id: String
  val resources: List<IResource>
  val vendor_metadata: JsonObject
  val logs: JsonObject

  fun findInteractionWith(uuid: String): IBlockInteraction {
    return interactions.lastOrNull { it.uuid == uuid }
        ?: throw ValidationException("Unable to find interaction on context: $uuid in [${interactions.map { it.uuid }}]")
  }

  fun findFlowWith(uuid: String): IFlow {
    return flows.find { it.uuid == uuid }
        ?: throw ValidationException("Unable to find flow on context: $uuid in ${flows.map { it.uuid }}")
  }

  fun findBlockOnActiveFlowWith(uuid: String): IBlock {
    return getActiveFlow().findBlockWith(uuid)
  }

  fun findNestedFlowIdFor(interaction: IBlockInteraction): String {
    val runFlowBlock = findFlowWith(interaction.flow_id).findBlockWith(interaction.block_id)
    return (runFlowBlock.config as IRunFlowBlockConfig).flow_id
  }

  fun getActiveFlowId(): String {
    return if (nested_flow_block_interaction_id_stack.isEmpty()) {
      first_flow_id
    } else {
      findNestedFlowIdFor(findInteractionWith(nested_flow_block_interaction_id_stack.last()))
    }
  }

  fun getActiveFlow() = findFlowWith(getActiveFlowId())

  fun isLastBlockOn(block: IBlock): Boolean {
    return !isNested() && block.isLastInFlow()
  }

  fun isNested(): Boolean {
    return nested_flow_block_interaction_id_stack.isNotEmpty()
  }
}

interface IContextWithCursor : IContext {
  override var cursor: ICursor
}

interface IContextInputRequired : IContext {
  override val cursor: ICursorInputRequired
}