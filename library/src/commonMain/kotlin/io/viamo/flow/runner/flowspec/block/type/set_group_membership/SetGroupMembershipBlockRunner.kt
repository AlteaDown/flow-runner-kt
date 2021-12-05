package io.viamo.flow.runner.flowspec.block.type.set_group_membership

import ValidationException
import io.viamo.flow.runner.domain.IRichCursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit

/**
 * Adds or removes a group from the contact.
 */
data class SetGroupMembershipBlockRunner(
  override val block: ISetGroupMembershipBlock,
  override val context: Context
) : IBlockRunner<Nothing?> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null
  override suspend fun run(cursor: IRichCursor): IBlockExit {
    return try {
      val group = context.groups.find { group -> group.group_key == block.config.group_key }
          ?: throw ValidationException("Cannot add contact to non-existent group ${block.config.group_key}")

      // Seriously?
      when (block.config.is_member) {
        "true" -> context.contact.addGroup(group)
        "false" -> context.contact.delGroup(group)
        else -> throw IllegalStateException(
          """
          this.block.config.is_member was = ${block.config.is_member}, but expected 'true' or 'false'. However, as I converted this from TS,
          the actual value we expect is not clear, so this implementation may need to be changed to the values we are receiving.
          """.trimIndent()
        )
      }

      block.setContactProperty(context)
      block.firstTrueOrNullBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      block.findDefaultBlockExitOrThrow()
    }
  }
}
