package io.viamo.flow.runner.flowspec.block.type.set_contact_property

import io.viamo.flow.runner.flowspec.block.IBlockConfigContactEditable
import io.viamo.flow.runner.flowspec.block.SetContactProperty

interface ISetContactPropertyBlockConfig: IBlockConfigContactEditable {
  override val set_contact_property: SetContactProperty
}

// TODO: This can be done in a kotlin way instead
/*
fun isSetContactPropertyConfig(thing: Any): thing is ISetContactPropertyBlockConfig {
  if (typeof thing == 'object' && thing !== null && 'set_contact_property' in thing) {
    val setContactProperty = (thing as ISetContactPropertyBlockConfig).set_contact_property
    return isSetContactProperty(setContactProperty)
  }
  return false
}

fun isSetContactProperty(thing: Any): thing is SetContactProperty {
  if (typeof thing == 'object' && thing !== null) {
    // noinspection SuspiciousTypeOfGuard
    return (
      'property_key' in thing &&
      'property_value' in thing &&
      typeof (thing as SetContactProperty).property_key == 'String' &&
      typeof (thing as SetContactProperty).property_value == 'String'
    )
  }
  return false
}
*/
