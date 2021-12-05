package io.viamo.flow.runner.flowspec

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Flow Containers hold a group of related Flows: https://floip.gitbook.io/flow-specification/flows#flows
 */
interface IContainer {
  /**
   * The version of the Flow Spec that this package is compliant with, e.g. 1.0.0-rc1
   */
  val specification_version: String

  /**
   * A globally unique identifier for this Container.  (See UUID Format: https://floip.gitbook.io/flow-specification/flows#uuid-format)
   *
   * @pattern ^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$
   */
  val uuid: String

  /**
   * A human-readable name for the Container content.
   */
  val name: String

  /**
   * An extended human-readable description of the content.
   */
  val description: String?

  /**
   * A set of key-value elements that is not controlled by the Specification,
   * but could be relevant to a specific vendor/platform/implementation.
   */
  val vendor_metadata: JsonObject?

  /**
   * A list of the Flows within the Container (see below)
   */
  val flows: List<IFlow>

  /**
   * A set of the Resources needed for executing the Flows in the Container, keyed by resource uuid.
   */
  val resources: List<IResource>
}

@Serializable
data class Container(
  override val specification_version: String,
  override val uuid: String,
  override val name: String,
  override val description: String?,
  override val vendor_metadata: JsonObject?,
  override val flows: List<Flow>,
  override val resources: List<Resource>
) : IContainer
