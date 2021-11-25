package io.viamo.flow.runner.domain

import com.benasher44.uuid.uuid4

/**
 * Implementation of {@link io.viamo.flow.runner.domain.IIdGenerator} that generates UUIDv4-format IDs.
 */
class IdGeneratorUuidV4 : IIdGenerator {
  override suspend fun generate() = uuid4().toString()
}
