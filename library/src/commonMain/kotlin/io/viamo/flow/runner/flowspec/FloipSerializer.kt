package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.type.advanced_select_one.AdvancedSelectOnePrompt
import io.viamo.flow.runner.flowspec.block.type.advanced_select_one.AdvancedSelectOnePromptConfig
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.MessagePrompt
import io.viamo.flow.runner.flowspec.block.type.message.MessagePromptConfig
import io.viamo.flow.runner.flowspec.block.type.numeric.NumericPrompt
import io.viamo.flow.runner.flowspec.block.type.numeric.NumericPromptConfig
import io.viamo.flow.runner.flowspec.block.type.open.OpenPrompt
import io.viamo.flow.runner.flowspec.block.type.open.OpenPromptConfig
import io.viamo.flow.runner.flowspec.block.type.select_many.SelectManyPrompt
import io.viamo.flow.runner.flowspec.block.type.select_many.SelectManyPromptConfig
import io.viamo.flow.runner.flowspec.block.type.select_one.SelectOnePrompt
import io.viamo.flow.runner.flowspec.block.type.select_one.SelectOnePromptConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object FloipSerializer {
  val module = SerializersModule {
    include(BlockSerializer.module)
    include(PromptSerializer.module)
    include(PromptConfigSerializer.module)
  }
}

private object BlockSerializer {
  val module = SerializersModule {
    polymorphic(IBlock::class) {
      subclass(MessageBlock::class)
    }
  }
}

object PromptConfigSerializer {
  val module = SerializersModule {
    polymorphic(IPromptConfig::class) {
      subclass(AdvancedSelectOnePromptConfig::class)
      subclass(MessagePromptConfig::class)
      subclass(NumericPromptConfig::class)
      subclass(OpenPromptConfig::class)
      subclass(SelectManyPromptConfig::class)
      subclass(SelectOnePromptConfig::class)
    }
  }
}

object PromptSerializer {
  val module = SerializersModule {
    polymorphic(BasePrompt::class) {
      subclass(AdvancedSelectOnePrompt::class)
      subclass(MessagePrompt::class)
      subclass(NumericPrompt::class)
      subclass(OpenPrompt::class)
      subclass(SelectManyPrompt::class)
      subclass(SelectOnePrompt::class)
    }
  }
}


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