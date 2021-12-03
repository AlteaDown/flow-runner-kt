package io.viamo.flow.runner.ext

import io.viamo.flow.runner.block.BlockSerializerModule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

inline fun <reified T> T.toJsonString(): String = Json.encodeToString(this)

fun Map<String, JsonElement>.toJsonObject() = JsonObject(this)
fun List<JsonElement>.toJsonArray() = JsonArray(this)
fun String?.toJsonPrimitive() = JsonPrimitive(this)
fun Number?.toJsonPrimitive() = JsonPrimitive(this)
fun Boolean?.toJsonPrimitive() = JsonPrimitive(this)

val JSON = Json {
  serializersModule = BlockSerializerModule.module
}

/** Converts a Map to a JsonObject, including all values, recursively */
fun Map<*, *>.toJsonElement(): JsonObject {
  return entries.associate { entry ->
    val key = entry.key
    if (key is String) {
      when (val entryValue = entry.value) {
        is JsonElement -> key to entryValue
        is Map<*, *> -> key to entryValue.toJsonElement()
        is List<*> -> key to entryValue.toJsonElement()
        is String -> key to entryValue.toJsonPrimitive()
        is Number -> key to entryValue.toJsonPrimitive()
        is Boolean -> key to entryValue.toJsonPrimitive()
        null -> key to JsonNull
        else -> error("Encountered unknown type for (key, value): ($key, ${entry.value})")
      }
    } else {
      error("The key of the map ($key) must be a String")
    }
  }
    .toJsonObject()
}

/** Converts a List to a JsonArray, including all items, recursively */
fun List<*>.toJsonElement(): JsonArray {
  return this.mapIndexed { index, entry ->
    when (entry) {
      is JsonElement -> entry
      is Map<*, *> -> entry.toJsonElement()
      is List<*> -> entry.toJsonElement()
      is String -> entry.toJsonPrimitive()
      is Number -> entry.toJsonPrimitive()
      is Boolean -> entry.toJsonPrimitive()
      null -> JsonNull
      else -> error("Encountered unknown type for array at index ($index), element: (${entry})")
    }
  }.toJsonArray()
}

fun JsonObject.getObject(key: String) = this[key]?.jsonObject
fun JsonObject.getArray(key: String) = this[key]?.jsonArray
fun JsonObject.getPrimitive(key: String) = this[key]?.jsonPrimitive
fun JsonObject.getString(key: String) = this[key]?.jsonPrimitive?.content!!
fun JsonObject.getNullableString(key: String) = this[key]?.jsonPrimitive?.contentOrNull
fun JsonObject.getInt(key: String) = this[key]?.jsonPrimitive?.int
fun JsonObject.getNullableInt(key: String) = this[key]?.jsonPrimitive?.intOrNull
fun JsonObject.getBoolean(key: String) = this[key]?.jsonPrimitive?.boolean
fun JsonObject.getNullableBoolean(key: String) = this[key]?.jsonPrimitive?.booleanOrNull
fun JsonObject.getFloat(key: String) = this[key]?.jsonPrimitive?.float
fun JsonObject.getNullableFloat(key: String) = this[key]?.jsonPrimitive?.floatOrNull
fun JsonObject.getLong(key: String) = this[key]?.jsonPrimitive?.long
fun JsonObject.getNullableLongOrNull(key: String) = this[key]?.jsonPrimitive?.longOrNull
