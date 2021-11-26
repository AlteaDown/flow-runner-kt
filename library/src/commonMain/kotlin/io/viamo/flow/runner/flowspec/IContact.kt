package io.viamo.flow.runner.flowspec

typealias ContactGroupResolver = ((group: IGroup) -> Unit)

/** Handles the mutliple types that a contact property can be, even though some are primitives.*/
sealed interface IContactPropertyType {
  sealed interface IContactPropertySealed : IContactProperty, IContactPropertyType
  sealed interface ContactPropertyResolver : IContactPropertyType {
    fun resolve(args: List<String>): IContactProperty?
  }

  sealed interface ContactGroupListSealed : List<IContactGroup>, IContactPropertyType
  sealed interface ContactGroupResolver : IContactPropertyType {
    fun resolve(args: List<String>): IGroup?
  }

  data class ContactString(val content: String) : IContactPropertyType

  val IContactPropertyType.string: String
    get() = if ((this is ContactString)) this.content else error("JsonPrimitive")
}

interface IContact {
  val id: String

  // TODO: was "[key: String]: IContactPropertyType", but really, properties should not be at this level. Why do properties exist at this level, but not individual Groups?
  val properties: Map<String, IContactPropertyType>

  var groups: MutableList<IContactGroup>

  /**
   * Set a property on this contact.
   * The value given will become the value of a new io.viamo.flow.runner."flow-spec".IContactProperty on the
   * contact. That property object should be returned.
   */
  fun setProperty(name: String, value: String?): IContactProperty

  /**
   * Get a property previously defined on this contact.
   * If no such propery exists, this may return null, else the
   * io.viamo.flow.runner."flow-spec".IContactProperty will be returned, which contains a String value.
   */
  fun getProperty(name: String): IContactProperty?

  /**
   * Add a group to this contact.
   * The group should be an an existing group within the flow context.
   * The value of the group must be copied into an io.viamo.flow.runner."flow-spec".IContactGroup existing under
   * the "groups" property of the contact.
   */
  fun addGroup(group: IGroup)

  /**
   * Remove a group from this contact.
   * The group should be an existing group within the flow context.
   * If the group exists in the "groups" property of the contact, it will
   * be removed or marked as removed. If it does not already exist, nothing
   * happens.
   */
  fun delGroup(group: IGroup)
}
