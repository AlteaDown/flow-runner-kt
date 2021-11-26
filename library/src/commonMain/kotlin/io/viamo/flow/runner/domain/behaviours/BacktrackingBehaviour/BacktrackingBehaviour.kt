package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

// interface IBacktrackingContext {
//   /**
//    * Current key into interaction hierarchy */
//   val cursor: Key
//
//   /**
//    * Hierarchical list of interactions */
//   val interactionStack: IStack
//
//   /**
//    * Ghost lists of interactions; we follow these stacks when stepping forward after a backtrack to receive suggestions. */
//   val ghostInteractionStacks: List<IStack>
// }
//
// interface IContextBacktrackingPlatformMetadata {
//   val backtracking: IBacktrackingContext
// }
//
// type BacktrackingCursor = IBacktrackingContext['cursor']
// type BacktrackingIntxStack = IBacktrackingContext['interactionStack']
//
// interface IBackTrackingBehaviour : IBehaviour {
//   rebuildIndex(): Unit
//
//   // regenerates prompt from previous interaction
//   // generates new prompt from new interaction + resets state to what was "interaction"'s moment
//   // todo: this should likely take in steps rather than interaction itself
//   jumpTo(interaction: IBlockInteraction, context: IContext): IRichCursor
//
//   peek(steps: Number?): IPrompt
// }
//
// class BacktrackingBehaviour : IBackTrackingBehaviour {
//   constructor(context: IContext, navigator: IFlowNavigator, promptBuilder: IPromptBuilder) {
//     this.initializeBacktrackingContext()
//   }
//
//   initializeBacktrackingContext()
//   {
//     val meta: IContextBacktrackingPlatformMetadata = (this.context.vendor_metadata as unknown)
//
//     if (meta.backtracking == null) {
//       meta.backtracking = {
//         cursor: createKey(),
//         interactionStack: createStack(),
//         ghostInteractionStacks: [],
//       }
//     }
//
//     if (meta.backtracking.interactionStack == null || meta.backtracking.cursor == null) {
//       meta.backtracking.cursor = createKey()
//       meta.backtracking.interactionStack = createStack()
//       this.rebuildIndex()
//     }
//   }
//
//   hasIndex()
//   {
//     val meta: IContextBacktrackingPlatformMetadata = (this.context.vendor_metadata as unknown)
//     return meta.backtracking.interactionStack != null && meta.backtracking.cursor != null
//   }
//
//   rebuildIndex()
//   {
//     val backtracking = ((this.context.vendor_metadata as unknown) as IContextBacktrackingPlatformMetadata).backtracking
//
//     val key = (backtracking.cursor = createKey())
//     val stack = (backtracking.interactionStack = createStack())
//
//     this.context.interactions.forEach(intx -> this.insertInteractionUsing(key, intx, stack))
//   }
//
//   insertInteractionUsing(key: BacktrackingCursor, interaction: IBlockInteraction, interactionStack: BacktrackingIntxStack): Unit
//   {
//     // check: are we repeating anything?
//     val keyForIntxOfRepeatedBlock = shallowIndexOfRightFrom(
//       key,
//       interactionStack,
//       intx -> (intx as IBlockInteraction).block_id == interaction.block_id
//     )
//     if (keyForIntxOfRepeatedBlock != null) {
//       // [Step In] Found a new stack to step into
//       this._stepIn(key, interactionStack, keyForIntxOfRepeatedBlock, interaction)
//       return
//     }
//
//     // check if any parent stacks start with a matching block; included current stack, but that should be caught above
//     val keyToBeginningOfStackWithHeadMatchingBlock = findHeadRightFrom(
//       key,
//       interactionStack,
//       intx -> (intx as IBlockInteraction).block_id == interaction.block_id
//     )
//     if (keyToBeginningOfStackWithHeadMatchingBlock != null) {
//       // [Step Out] Found an stack to continue on from
//       this._stepOut(keyToBeginningOfStackWithHeadMatchingBlock, interactionStack, interaction, key)
//       return
//     }
//
//     // push onto current stack + update key with new size - 1
//     last(key)![STACK_KEY_ITERATION_INDEX] = _append(interaction, getStackFor(key, interactionStack))-1
//   }
//
//   _stepOut(
//   keyToBeginningOfStackWithHeadMatchingBlock: Key,
//   interactionStack: BacktrackingIntxStack,
//   interaction: IBlockInteraction,
//   key: BacktrackingCursor
//   )
//   {
//     val stack: IStack = getStackFor(keyToBeginningOfStackWithHeadMatchingBlock, interactionStack)
//     _loop(stack, [interaction])
//
//     val newStackKey: StackKey = createStackKeyForLastIterAndLastIndexOf(stack)
//     moveStackIndexTo(newStackKey, keyToBeginningOfStackWithHeadMatchingBlock)
//
//     key.splice(0, Number.MAX_VALUE, ... keyToBeginningOfStackWithHeadMatchingBlock)
//   }
//
//   _stepIn(
//   key: BacktrackingCursor,
//   interactionStack: BacktrackingIntxStack,
//   keyForIntxOfRepeatedBlock: Key,
//   interaction: IBlockInteraction
//   )
//   {
//     val iteration = getIterationFor(key, interactionStack)
//     // todo: abstract key operations
//     val i = last(keyForIntxOfRepeatedBlock)![STACK_KEY_ITERATION_INDEX]
//
//     // take from i to the end
//     val nestedIteration = iteration.splice(i)
//
//     // append taken items (returned); append [intx]
//     val nestedStack = createStack(nestedIteration)
//
//     // append onto parent <-- todo: update head! // todo: let's use io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour._append() here
//     iteration.push(nestedStack)
//     _loop(nestedStack, [interaction])
//
//     // update key like: replace current key w/ found index; append [1 (new stack), 1 (second iteration)]
//     last(key)![STACK_KEY_ITERATION_INDEX] = i
//     key.push(createStackKey(1, 0))
//   }
//
//   suspend fun jumpTo(interaction: IBlockInteraction, context: IContext): IRichCursor {
//     val backtracking = ((context.vendor_metadata as unknown) as IContextBacktrackingPlatformMetadata).backtracking
//
//     // find a key for provided past interaction
//     val keyForLastOccurrenceOfInteraction = deepIndexOfFrom(
//       createKey(), // begins search from beginning -- todo: search from right?
//       backtracking.interactionStack,
//       ({ uuid }) -> uuid == interaction.uuid
//     )
//
//     if (keyForLastOccurrenceOfInteraction == null) {
//       throw new ValidationException ('Unable to find destination interaction in backtracking stack for jumpTo()')
//     }
//
//     // todo: if something hasn't changed, can we extend this ghost rather than creating anew?
//
//     // create ghost stack to follow after jumping back
//     backtracking.ghostInteractionStacks.push(cloneDeep(backtracking.interactionStack))
//
//     // jump context.interactions back in time
//     val discarded = context.interactions.splice(
//       // truncate interactions list to pull us back in time; including provided intx
//       findLastIndex(context.interactions, interaction),
//       context.interactions.size
//     )
//
//     // step out of nested flows that we've truncated
//     forEachRight(discarded, intx ->
//     intx.uuid == last(context.nested_flow_block_interaction_id_stack) ? context.nested_flow_block_interaction_id_stack.pop() : null
//     )
//
//     // update interactionStack to match
//
//     // todo: should this truncate be inclusive or exclusive?
//     //       - it should remove found interaction; aka point should result in none; how does this change references to backtracking.cursor thenceforth?
//
//     val deepestStackKeyForIntx = last(keyForLastOccurrenceOfInteraction)!
//     val keyToTruncateFrom = cloneKeyAndMoveTo(
//       createStackKey(
//         deepestStackKeyForIntx[STACK_KEY_ITERATION_NUMBER],
//         deepestStackKeyForIntx[STACK_KEY_ITERATION_INDEX] - 1
//       ), // slide left one so that we free current spot up
//       keyForLastOccurrenceOfInteraction,
//       backtracking.interactionStack
//     )
//
//     deepTruncateIterationsFrom(keyToTruncateFrom, backtracking.interactionStack)
//
//     // set backtracking cursor to match
//     backtracking.cursor =
//         keyToTruncateFrom // keyForLastOccurrenceOfInteraction // todo: this now points at a null; should it be "keyToTruncateFrom"?
//
//     // todo: This navigateTo() is going to append an interaction onto context.interactions --> verify that context.interactions.splice() accounts for that
//     // todo: this should provide a sourceId="" in meta so that we can tie these together
//
//     return this.navigator.navigateTo(findBlockOnActiveFlowWith(interaction.block_id, context), context)
//   }
//
//   suspend fun peek(steps = 1): IPrompt {
//     var _steps = steps
//     val intx = findLast(this.context.interactions, ({ type }) ->!includes(NON_INTERACTIVE_BLOCK_TYPES, type) && --_steps == 0)
//
//     if (intx == null || _steps > 0) {
//       throw new ValidationException ("Unable to backtrack to an interaction that far back ${JSON.Stringify({ steps })}")
//     }
//
//     val block = findBlockWith(intx.block_id, findFlowWith(intx.flow_id, this.context))
//
//     val prompt = await this.promptBuilder.buildPromptFor(block, intx)
//     if (prompt == null) {
//       throw new ValidationException (
//           "Unable to build a prompt for ${
//             JSON.Stringify({
//               context: this.context.id,
//               intx,
//               block,
//             })
//           }"
//           )
//     }
//
//     return Object.assign(prompt, { value: intx.value })
//   }
//
//   findIndexOfSuggestionFor(
//   { block_id }: IBlockInteraction, key: Key, stack: IStack): Key?
//   {
//     val keyForSuggestion = deepIndexOfFrom(key, stack, intx -> (intx as IBlockInteraction).block_id == block_id)
//
//     if (keyForSuggestion != null) {
//       return keyForSuggestion
//     }
//
//     // also scan next iteration when we're on last index of current iteration
//     val lastIndexOfIteration = getIterationFor(key, stack).size - 1
//     val shouldCheckNextIteration = last(key)![STACK_KEY_ITERATION_INDEX] == lastIndexOfIteration
//     if (!shouldCheckNextIteration) {
//       return
//     }
//
//     val keyForNextIteration = moveStackIndexTo(
//       createStackKey(0, 0),
//       cloneDeep(key)
//     ) // todo: use io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.cloneKeyAndMoveTo()
//     return deepIndexOfFrom(keyForNextIteration, stack, intx -> (intx as IBlockInteraction).block_id == block_id)
//   }
//
//   postInteractionCreate(interaction: IBlockInteraction, _context: IContext): IBlockInteraction
//   {
//     val {
//     backtracking: { cursor: key,
//     // interactionStack,
//     ghostInteractionStacks,
//   },
//   } = (this.context.vendor_metadata as unknown)
//
//     if (ghostInteractionStacks.size == 0) {
//       // can't suggest when we don't have ghost interactions from the past
//       return interaction
//     }
//
//     if (!this.hasIndex()) {
//       this.rebuildIndex()
//     }
//
//     val lastGhost = last(ghostInteractionStacks)
//     if (lastGhost == null) {
//       throw new Error ('whups no ghost')
//     }
//
//     val keyForSuggestion = this.findIndexOfSuggestionFor(interaction, key, lastGhost)
//     if (keyForSuggestion == null) {
//       return interaction
//     }
//
//     // todo: this should be looking at ghostInteractionStacks and not interactionStack ???
//     interaction.value = (getEntityAt(keyForSuggestion, lastGhost) as IBlockInteraction).value
//
//     // need to splice out things between key + keyForSuggestion so that key points to both interaction and suggestion
//     if (keyForSuggestion.join() !== key.join()) {
//       ghostInteractionStacks.forEach(
//         (
//             ghostInteractionStack // todo: fix up syncGhost now that we're multi-tracking
//             ) -> this.syncGhostTo(key, keyForSuggestion, ghostInteractionStack)) // todo: reverse these keys to match signature?!
//     }
//
//     return interaction
//   }
//
//   /**
//    * A hierarchical deep splice to remove items between two keys, hoisting deepest iteration to main depth.
//    * ghost=[[0, 1], [1, 3], [0, 1], [1, 2]]
//    * main=[[0, 1], [1, 4]] */
//   syncGhostTo(key: Key, keyForSuggestion: Key, ghost: IStack)
//   {
//     // : Key {
//     // todo: refactor this into stack::deepSplice(): List<Item>
//     //       if we provide items (remainderOfCurrentGhostIteration), this becomes two logical pieces splice() + deepSplice()
//
//     if (keyForSuggestion.size < key.size) {
//       throw new ValidationException ("Unable to sync up ghost; ${JSON.Stringify({ key, keyForSuggestion })}")
//     }
//
//     if (keyForSuggestion.size == key.size && keyForSuggestion.toString() == key.toString()) {
//       return // keyForSuggestion
//     }
//
//     var isAtGreaterDepth = keyForSuggestion.size > key.size
//     var stackKeyForSuggestion: StackKey = last(keyForSuggestion)!
//     // find our keepers = remainderOfCurrentGhostIteration
//     val iterationForSuggestion = getIterationFor(keyForSuggestion, ghost)
//     val remainderOfCurrentGhostIteration = iterationForSuggestion.splice(
//       stackKeyForSuggestion[STACK_KEY_ITERATION_INDEX],
//       Number.MAX_VALUE
//     )
//
//     // discard iterations up to + including one with keepers
//     var wasEmptyStackLeftOver = false
//     if (isAtGreaterDepth) {
//       val stack = (getStackFor(keyForSuggestion, ghost)).stack
//       stack.splice(0, stackKeyForSuggestion[STACK_KEY_ITERATION_NUMBER] + 1)
//       wasEmptyStackLeftOver = stack.size == 0
//       keyForSuggestion.pop() // update suggestion key to point to containing stack
//     }
//
//     // splice keepers onto containing stack at suggestedKeyIterAndIndex; discarding # of items between
//     stackKeyForSuggestion = last(keyForSuggestion)!
//     val deepestStackKeyIndex =
//         keyForSuggestion.size == key.size ? last(key)![STACK_KEY_ITERATION_INDEX] : stackKeyForSuggestion[STACK_KEY_ITERATION_INDEX]
//     isAtGreaterDepth = keyForSuggestion.size > key.size
//     val itemsBetweenKeyAndGhost = isAtGreaterDepth
//     ? 0 // when still different depth after .pop()
//     : stackKeyForSuggestion[STACK_KEY_ITERATION_INDEX] - deepestStackKeyIndex // {sugKey's index} - {key's index} when same depth
//
//     val containingIterationForSuggestion = getIterationFor(keyForSuggestion, ghost)
//     containingIterationForSuggestion.splice(
//       deepestStackKeyIndex,
//       itemsBetweenKeyAndGhost + (wasEmptyStackLeftOver ? 1 : 0),
//     ...remainderOfCurrentGhostIteration
//     )
//
//     // when still at greater depth: repeat the ordeal
//     if (isAtGreaterDepth) {
//       this.syncGhostTo(key, keyForSuggestion, ghost)
//     }
//   }
//
//   postInteractionComplete(interaction: IBlockInteraction, _context: IContext): Unit
//   {
//     val {
//     backtracking: { cursor: key, interactionStack, ghostInteractionStacks },
//   } = (this.context.vendor_metadata as unknown)
//
//     this.insertInteractionUsing(key, interaction, interactionStack)
//
//     if (ghostInteractionStacks.size == 0) {
//       // can't suggest when we don't have ghost interactions from the past
//       return
//     }
//
//     ghostInteractionStacks.forEach(ghostInteractionStack -> {
//     // update ghost to account for changes while stepping forward
//     val ghostIntx = forceGet(
//       key,
//       ghostInteractionStack
//     ) // todo: this should actually be a io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.deepFindFrom() w/in current iteration
//     // when ghost is absent or ghost value matches
//     if (!isEntity(ghostIntx) || isEqual(interaction.value, (ghostIntx as IBlockInteraction).value)) {
//       return // do nothing
//     }
//
//     // when interaction's value differs from interaction@ghost's value
//     // remove the possibility of suggesting from future -- todo: verify this logic -- can we be this destructive?
//
//     // assumption: keys point to same points in both ghost and main stacks
//     truncateIterationFrom(key, ghostInteractionStack) // todo: should this be inclusive or exclusive?
//   })
//   }
// }
