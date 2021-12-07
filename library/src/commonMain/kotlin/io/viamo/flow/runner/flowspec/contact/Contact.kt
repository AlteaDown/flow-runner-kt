package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.flowspec.IContext
import io.viamo.flow.runner.flowspec.IGroup
import io.viamo.flow.runner.flowspec.block.SetContactProperty
import io.viamo.flow.runner.flowspec.block.evaluateToString
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
  override val id: String,
  override val properties: MutableMap<String, ContactPropertyType>,
  override var groups: MutableList<ContactGroup> = mutableListOf()
) : IContact {

  override fun setProperty(name: String, value: String?): IContactProperty {
    return ContactPropertyType.ContactProperty(
      value = value,
      contact_property_field_name = name,
      created_at = createFormattedDate(),
      updated_at = createFormattedDate(),
    )
      .also { properties[name] = it }
  }

  override fun getProperty(name: String): IContactProperty? {
    if (properties[name] == null) {
      return null
    }
    return properties[name] as IContactProperty
  }

  override fun addGroup(group: IGroup) {
    val existingGroup = groups.find { it.group_key == group.group_key }
    if (existingGroup == null) {
      groups.add(ContactGroup(group, updated_at = createFormattedDate()))
    } else {
      existingGroup.updated_at = createFormattedDate()
      existingGroup.deleted_at = null
    }
  }

  override fun delGroup(group: IGroup) {
    val groupToDelete = groups.find { it.group_key == group.group_key }
    if (groupToDelete != null) {
      createFormattedDate().let { now ->
        groupToDelete.deleted_at = now
        groupToDelete.updated_at = now
      }
    }
  }

  //TODO: Move to Contact
  fun setSingleContactProperty(property: SetContactProperty, context: IContext) {
    context.contact.setProperty(
      name = property.property_key,
      value = evaluateToString(property.property_value, /* TODO: Was createEvalContextFrom(context) */ context)
    )
  }
}

interface IContact {
  val id: String

  // TODO: was "[key: String]: IContactPropertyType", but really, properties should not be at this level. Why do properties exist at this level, but not individual Groups?
  val properties: Map<String, ContactPropertyType>

  val groups: List<IContactGroup>

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