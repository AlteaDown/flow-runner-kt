package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.flowspec.ContactGroup
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
