package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate

class ContactProperty(
  contact_property_field_name: String,
  override var value: String?,
  override val created_at: String = createFormattedDate(),
  override var updated_at: String = createFormattedDate(),
  override val deleted_at: String? = null,
) : IContactPropertyType.IContactPropertySealed {

  // TODO: Consider using copy mechanics for this
  override var contact_property_field_name: String = contact_property_field_name
    set(value) {
      field = value
      this.updated_at = createFormattedDate()
    }

  // TODO: Consider using copy mechanics for this
  override var __value__
    get(): String? = this.value
    set(value) {
      this.value = value
      this.updated_at = createFormattedDate()
    }
}

