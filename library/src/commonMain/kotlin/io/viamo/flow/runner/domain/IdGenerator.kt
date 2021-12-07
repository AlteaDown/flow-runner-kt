package io.viamo.flow.runner.domain

import com.benasher44.uuid.uuid4

/**
 * Interface for a class that can generate unique IDs. The {@link io.viamo.flow.runner.domain.FlowRunner} needs to generate unique IDs internally,
 * but different projects may have different standards/requirements for ID formats. An implementation of io.viamo.flow.runner.domain.IIdGenerator
 * must be injected into the io.viamo.flow.runner.domain.FlowRunner, which it will use to generate IDs when needed.
 *
 * For a reference version, see {@link io.viamo.flow.runner.domain.IdGeneratorUuidV4}.
 */
interface IIdGenerator {
  suspend fun generate(): String
}

/**
 * Implementation of {@link io.viamo.flow.runner.domain.IIdGenerator} that generates UUIDv4-format IDs.
 */
class IdGeneratorUuidV4 : IIdGenerator {
  override suspend fun generate() = uuid4().toString()
}