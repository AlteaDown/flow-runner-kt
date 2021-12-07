package io.viamo.flow.runner.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NothingNullableSerializer : KSerializer<Nothing?> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Nothing") {
    isNullable = true
  }

  override fun serialize(encoder: Encoder, value: Nothing?) {
    encoder.encodeNull()
  }

  override fun deserialize(decoder: Decoder): Nothing? {
    return null
  }
}
