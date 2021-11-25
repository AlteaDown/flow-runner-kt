import io.viamo.flow.runner.domain.prompt.INoPromptConfig
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.ISetGroupMembershipBlock
import io.viamo.flow.runner.model.block.ISetGroupMembershipBlockConfig

/**
 * Adds or removes a group from the contact.
 */
data class SetGroupMembershipBlockRunner(
  override val block: ISetGroupMembershipBlock,
  override val context: IContext
) : IBlockRunner<Nothing?, ISetGroupMembershipBlockConfig, INoPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null
  override suspend fun run(_cursor: IRichCursor<Nothing?, ISetGroupMembershipBlockConfig, INoPromptConfig>): IBlockExit {
    return try {
      val group = this.context.groups.find { group -> group.group_key == this.block.config.group_key }
          ?: throw ValidationException("Cannot add contact to non-existent group ${this.block.config.group_key}")

      // Seriously?
      when (this.block.config.is_member) {
        "true" -> this.context.contact.addGroup(group)
        "false" -> this.context.contact.delGroup(group)
        else -> throw IllegalStateException(
          """
          this.block.config.is_member was = ${this.block.config.is_member}, but expected 'true' or 'false'. However, as I converted this from TS,
          the actual value we expect is not clear, so this implementation may need to be changed to the values we are receiving.
          """.trimIndent()
        )
      }

      setContactProperty(this.block, this.context)
      firstTrueOrNullBlockExitOrThrow(this.block, this.context)
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOrThrow(this.block)
    }
  }
}
