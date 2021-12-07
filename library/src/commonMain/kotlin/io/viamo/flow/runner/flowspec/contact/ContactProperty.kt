package io.viamo.flow.runner.flowspec.contact

import io.viamo.flow.runner.flowspec.IGroup
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
/** Handles the mutliple types that a contact property can be, even though some are primitives.*/
sealed class ContactPropertyType {
  @Serializable
  data class ContactProperty(
    override val contact_property_field_name: String,
    override val created_at: Instant,
    override val updated_at: Instant,
    override val value: String?,
    override val deleted_at: Instant? = null,
  ) : ContactPropertyType(), IContactProperty {
    override val __value__: String? = value
  }

  @Serializable
  sealed class ContactPropertyResolver : ContactPropertyType() {
    abstract fun resolve(args: List<String>): IContactProperty?
  }

  @Serializable
  sealed class ContactGroupListSealed : ContactPropertyType(), List<IContactGroup>

  @Serializable
  sealed class ContactGroupResolver : ContactPropertyType() {
    abstract fun resolve(args: List<String>): IGroup?
  }

  data class ContactString(val content: String) : ContactPropertyType()

  val ContactPropertyType.string: String
    get() = if ((this is ContactString)) this.content else error("JsonPrimitive")
}

interface IContactProperty {
  val contact_property_field_name: String
  val created_at: Instant
  val updated_at: Instant
  val deleted_at: Instant?
  val __value__: String?
  val value: String?
}