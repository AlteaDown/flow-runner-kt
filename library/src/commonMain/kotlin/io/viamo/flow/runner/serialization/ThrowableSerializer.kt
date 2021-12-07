package io.viamo.flow.runner.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ThrowableSerializer : KSerializer<Throwable?> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Throwable") {
    isNullable = true
  }

  override fun serialize(encoder: Encoder, value: Throwable?) {
    value?.apply {
      encoder.encodeNullableSerializableValue(String.serializer(), message)
      encoder.encodeNullableSerializableValue(ThrowableSerializer, cause)
    }
  }

  override fun deserialize(decoder: Decoder): Nothing? {
    return null
  }
}