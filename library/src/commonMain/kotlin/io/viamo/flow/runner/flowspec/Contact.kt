package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate

data class Contact(
  override val id: String,
  override val properties: MutableMap<String, IContactPropertyType>,
  override var groups: MutableList<IContactGroup> = mutableListOf()
) : IContact {

  override fun setProperty(name: String, value: String?): IContactProperty {
    return ContactProperty(
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
      groups.add(ContactGroup(group))
    } else {
      existingGroup.updated_at = createFormattedDate()

      // make sure this group isn't marked as deleted
      existingGroup.deleted_at = null
    }
  }

  override fun delGroup(toRemove: IGroup) {
    val group = groups.find { group -> group.group_key == toRemove.group_key }
    if (group != null) {
      createFormattedDate().let { now ->
        group.deleted_at = now
        group.updated_at = now
      }
    }
  }
}
