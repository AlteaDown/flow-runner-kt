package io.viamo.flow.runner.model.block

interface SetContactProperty {
  val property_key: String
  val property_value: String
}

/**
 * All blocks have a standard capability to specify how a contact property should be updated.
 * This update shall happen immediately prior to following the exit node out of the block.
 * This is specified via the optional set_contact_property object within the Block config
 */
// TODO: Use Kotlin Serialization to handle this
interface IBlockConfig : Map<String, Any> {
  val set_contact_property: SetContactProperty?
}