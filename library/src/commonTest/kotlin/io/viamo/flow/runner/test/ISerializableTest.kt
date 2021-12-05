package io.viamo.flow.runner.test

import kotlin.js.JsName

/**
 * Test needed to check serialization
 */
interface ISerializableTest {

  /***
   *
   */
  @JsName("is_serializable_to_json_then_to_object")
  fun `is serializable to json then to object`()
}