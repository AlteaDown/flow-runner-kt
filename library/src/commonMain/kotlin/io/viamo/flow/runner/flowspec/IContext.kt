package io.viamo.flow.runner.flowspec

import ValidationException
import io.viamo.flow.runner.block.run_flow.IRunFlowBlockConfig
import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.IIdGenerator
import io.viamo.flow.runner.domain.IdGeneratorUuidV4
import io.viamo.flow.runner.domain.createFormattedDate
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
  val resources: IResources
  val vendor_metadata: JsonObject
  val logs: JsonObject

  suspend fun createContextDataObjectFor(
    contact: IContact,
    groups: List<IGroup>,
    userId: String,
    orgId: String,
    flows: List<IFlow>,
    languageId: String,
    mode: SupportedMode = SupportedMode.OFFLINE,
    resources: List<IResource> = emptyList(),
    idGenerator: IIdGenerator = IdGeneratorUuidV4()
  ): IContext {
    return Context(
      id = idGenerator.generate(),
      created_at = createFormattedDate(),
      delivery_status = DeliveryStatus.QUEUED,
      user_id = userId,
      org_id = orgId,
      mode = mode,
      language_id = languageId,
      contact = contact,
      groups = groups,
      flows = flows,
      first_flow_id = flows[0].uuid,
      resources = resources,
    )
  }

  fun findInteractionWith(uuid: String): IBlockInteraction {
    return interactions.lastOrNull { it.uuid == uuid }
        ?: throw ValidationException("Unable to find interaction on context: $uuid in [${interactions.map { it.uuid }}]")
  }

  fun findFlowWith(uuid: String): IFlow {
    return flows.find { it.uuid == uuid }
        ?: throw ValidationException("Unable to find flow on context: $uuid in ${flows.map { it.uuid }}")
  }

  fun findBlockOnActiveFlowWith(uuid: String): IBlock {
    return findBlockWith(uuid, getActiveFlowFrom())
  }

  fun findNestedFlowIdFor(interaction: IBlockInteraction): String {
    val flow = findFlowWith(interaction.flow_id)
    val runFlowBlock = findBlockWith(interaction.block_id, flow)
    return (runFlowBlock.config as IRunFlowBlockConfig).flow_id
  }

  fun getActiveFlowIdFrom(): String {
    return if (nested_flow_block_interaction_id_stack.isEmpty()) {
      first_flow_id
    } else {
      findNestedFlowIdFor(findInteractionWith(nested_flow_block_interaction_id_stack.last()))
    }
  }

  fun getActiveFlowFrom() = findFlowWith(getActiveFlowIdFrom())

  fun isLastBlockOn(block: IBlock): Boolean {
    return !isNested() && isLastBlock(block)
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