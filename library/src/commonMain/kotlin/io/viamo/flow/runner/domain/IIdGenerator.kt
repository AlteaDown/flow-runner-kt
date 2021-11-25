package io.viamo.flow.runner.domain

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
