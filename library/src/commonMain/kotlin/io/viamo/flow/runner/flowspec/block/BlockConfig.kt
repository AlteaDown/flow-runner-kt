package io.viamo.flow.runner.flowspec.block

interface SetContactProperty {
  val property_key: String
  val property_value: String
}

/**
 * All blocks have a standard capability to specify how a contact property should be updated.
 * This update shall happen immediately prior to following the exit node out of the block.
 * This is specified via the optional set_contact_property object within the Block config
 */
interface IBlockConfig

interface IBlockConfigContactEditable : IBlockConfig {
  val set_contact_property: SetContactProperty?
}