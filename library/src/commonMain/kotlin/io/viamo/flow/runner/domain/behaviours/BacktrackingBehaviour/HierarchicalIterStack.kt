package io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour

//
//
//
//type IterationIndex = number
//type IterationNumber = number
//
//// String of 'stack' correlates to io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.IStack['stack'] prop
//type StackKey = ['stack', IterationNumber, IterationIndex]
//
////(IterationIndex | StackKey)[] // todo: are we only using a list of StackKey now? Eg. always in a stack
//type Key = List<StackKey>
//
//interface IEntity {
//  val uuid: String
//}
//
//type Iteration = (IEntity | IStack)[]
//
//interface IStack {
//  // todo: rename this so it's not "stack.stack" <-- rename stack.stack to stack.iterations
//  // todo: rename to _stack, "private" becuase most manipulations should happen via behaviour provided
//  val stack: List<Iteration>
//
//  // todo: ensure head is updated when manipulating an iteration
//  val head: IEntity?
//}
//
//type Item = IEntity | Iteration | IStack
//type IEntityMatcher = (x: IEntity) -> boolean
//
//val STACK_KEY_ITERATION_NUMBER = 1
//val STACK_KEY_ITERATION_INDEX = 2
//
//val DEFAULT_JOIN_SEPARATOR_MATCHER = /,/g
//
//fun createStack(firstIteration: Iteration = []): IStack {
//  return createStackFrom([firstIteration])
//}
//
//fun createStackFrom(iterations: List<Iteration>): IStack {
//  val stack: IStack = {stack: iterations}
//  stack.head = _findHeadOn(stack)
//  return stack
//}
//
//fun _findHeadOn(stack: IStack): IEntity? {
//  if (isStackEmpty(stack)) {
//    return
//  }
//
//  // [1st-iteration][1st-element]
//  val first = stack.stack[0][0]
//
//  return isEntity(first) ? first : _findHeadOn(first)
//}
//
///**
// * These keys are a bit magical: They're a list of indexes, organized hierarchically.
// * Each item in the outermost list represents a layer of nesting.
// * Each number itself is an index into our call stack.
// * We've kept these as a simple list of number|tuples so that we can do a simple join for retrievals.
// * We've used a tuple for stacks so that pop and rollup are similarly trivial.
// *
// * Example using numbers rather than Entities for brevity:
// * {stack: [[ ]]} == [['stack', 0, -1]]
// *           ^
// * {stack: [[1]]} == [['stack', 0, 0]]
// *           ^
// * Here we're nestled into base stack, 0th iteration, 3rd index
// * {stack: [[1, 2, 3, 4]]} --> [['stack', 0, 3]]
// *                    ^
// *
// * Here we're nested by one stack, 1st iteration, 0th index
// * {stack: [[1, 2, {stack: [[3, 4], [3]]}]]} --> [['stack', 0, 3], ['stack', 1, 0]]
// *                                   ^
// */
//fun createKey(index = -1, iteration = 0): Key {
//  // "-1" so that the typing needn't allow nulls, it's a non-existent value.
//  return [createStackKey(iteration, index)]
//}
//
//fun createStackKey(iteration: Number, index: Number): StackKey {
//  return ['stack', iteration, index]
//}
//
//fun isEntityAt(key: Key, stack: IStack): Boolean {
//  return isEntity(forceGet(key, stack))
//}
//
//fun isEntity(subject: Item): subject is IEntity {
//  return subject != null && 'uuid' in subject
//}
//
//fun isStack(subject: Item): subject is IStack {
//  return subject != null && 'stack' in subject
//}
//
//fun isIteration(subject: Item): subject is Iteration {
//  return isArray(subject) && !isEntity(subject) && !isStack(subject)
//}
//
//fun forceGet(key: Key, stack: IStack): Item {
//  // stacks are nested, so we just make a 2nd pass to cover all commas at once
//  return lodashGet(stack, key.join().replace(DEFAULT_JOIN_SEPARATOR_MATCHER, '.'))
//}
//
//fun getEntityAt(key: Key, stack: IStack): IEntity {
//  val entity = forceGet(key, stack)
//  if (entity == null || !isEntity(entity)) {
//    throw new ValidationException("Unable to find entity at ${key}")
//  }
//
//  return entity
//}
//
//fun isStackEmpty({stack}: IStack): Boolean {
//  return stack.size == 0 || stack[0].size == 0
//}
//
//fun getIterationFor(key: Key, stack: IStack): Iteration {
//  val containingStack = getStackFor(key, stack)
//  val iterationNumber = lodashLast(key)![STACK_KEY_ITERATION_NUMBER]
//  val iteration = containingStack.stack[iterationNumber]
//
//  if (!isIteration(iteration)) {
//    throw new ValidationException("Unable to find iteration one up from ${key}")
//  }
//
//  return iteration
//}
//
//fun getStackFor(key: Key, stack: IStack): IStack {
//  if (key.size == 0) {
//    throw new ValidationException("An empty key doesn't have a containing stack -- ${key}")
//  }
//
//  if (key.size == 1) {
//    return stack
//  }
//
//  val containingStack = forceGet(key.slice(0, -1), stack)
//  if (!isStack(containingStack)) {
//    throw new ValidationException("Unable to find stack one up from ${key}")
//  }
//
//  return containingStack
//}
//
//fun _insertAt(i: Number, entity: IEntity, iter: Iteration): (IEntity | IStack)[] {
//  // todo: update head + tail
//  return iter.splice(i, 0, entity)
//}
//
//fun _replaceAt(i: Number, entity: IEntity, iter: Iteration): Item {
//  // todo: update head + tail
//  return iter.splice(i, 1, entity)
//}
//
//fun _append(item: IEntity | IStack, stack: IStack): Number {
//  val length = lodashLast(stack.stack)!.push(item)
//  if (stack.stack.size == 1 && length == 1) {
//    stack.head = _findHeadOn(stack)
//  }
//
//  return length
//}
//
//fun _stepIn(stack: IStack, firstIteration: Iteration = []) {
//  return _append(createStack(firstIteration), stack)
//}
//
//fun _loop(stack: IStack, nextIteration: Iteration = []) {
//  stack.stack.push(nextIteration)
//  return stack
//}
//
///**
// * Remove tail end of current iteration _after_ cursor and up hierarchy as well. */
//fun deepTruncateIterationsFrom(key: Key, stack: IStack) {
//  truncateIterationFrom(key, stack)
//  getStackFor(key, stack).stack.splice(lodashLast(key)![STACK_KEY_ITERATION_NUMBER] + 1, Number.MAX_VALUE)
//
//  if (key.size <= 1) {
//    return
//  }
//
//  // get containing stack + repeat
//  deepTruncateIterationsFrom(key.slice(0, -1), stack)
//}
//
///**
// * Remove tail end of current iteration _after_ provided cursor. */
//fun truncateIterationFrom(key: Key, stack: IStack): Iteration {
//  if (key.size == 0) {
//    return []
//  }
//
//  // get iter + splice from that index
//  return getIterationFor(key, stack).splice(lodashLast(key)![STACK_KEY_ITERATION_INDEX] + 1, Number.MAX_VALUE)
//}
//
//fun cloneKeyAndMoveTo(stackKey: StackKey, key: Key, stack: IStack): Key {
//  val duplicateKey = cloneDeep(key)
//
//  val duplicateKeyAtNewPosition = [...duplicateKey.slice(0, -1), stackKey]
//
//  // todo: how is io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.forceGet() typed as Item -- this could be "null"
//  val x: Item = forceGet(duplicateKeyAtNewPosition, stack)
//  if (x == null) {
//    throw new ValidationException("Unable to find item at ${key}")
//  }
//
//  return duplicateKeyAtNewPosition
//}
//
///**
// * Replace last stack key with {dest}, while retaining key reference. */
//fun moveStackIndexTo(dest: StackKey, key: Key): Key {
//  key.splice(key.size - 1, 1, dest)
//  return key
//}
//
//fun createStackKeyForLastIterAndLastIndexOf({stack}: IStack): StackKey {
//  return createStackKey(Math.max(stack.size - 1, 0), Math.max(lodashLast(stack)!.size - 1, 0))
//}
//
///**
// * Used for stepping out; searching heads from inner to outer to see if we're pulling head of a different iteration.
// * @return key - when found: a key pointing to an io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.IStack (rather than a leaf node as per all other instances.
// *                           However, pls pay special attention that a key pointing to a stack is going to be one up,
// *                           and for the case of the root stack, this will be an empty key like: [].
// *                otherwise: null
// */
//
//// todo: what exactly is our domain logic where we'd nested at [0,0] ? I don't think it's possible,
////       because right now we're going to append an iteration to containing stack when this happens
////       as a method of stepping out
//
//fun findHeadRightFrom(key: Key, stack: IStack, matcher: IEntityMatcher): Key? {
//  val containingStack = getStackFor(key, stack)
//
//  if (!isStackEmpty(containingStack) && matcher(containingStack.head!)) {
//    // create a key to [iter:0][i:0] of current stack
//    return cloneKeyAndMoveTo(createStackKey(0, 0), key, stack)
//  }
//
//  // containingStack is stack when we're at root
//  return isStackEmpty(containingStack) || containingStack == stack ? null : findHeadRightFrom(key.slice(0, -1), stack, matcher)
//}
//
///**
// * Used for stepping in; searching right-to-left for a particular entity for repetition.
// * We only care about top-level Entities. */
//fun shallowIndexOfRightFrom(key: Key, stack: IStack, matcher: IEntityMatcher): Key? {
//  // todo: what should happen when we get to 0 and 0 is a stack?
//
//  val subject = forceGet(key, stack)
//
//  if (isEntity(subject) && matcher(subject)) {
//    return key
//  }
//
//  val deepestStackKey: StackKey = lodashLast(key) as StackKey
//  val i: Number = deepestStackKey[STACK_KEY_ITERATION_INDEX]
//  if (i <= 0) {
//    return
//  }
//
//  val deepestStackKeyShiftedLeft: StackKey = ['stack', deepestStackKey[STACK_KEY_ITERATION_NUMBER], i - 1]
//  return shallowIndexOfRightFrom([... key.slice(0, -1), deepestStackKeyShiftedLeft], stack, matcher) as Key
//}
//
///**
// * Recursive left search of hierarchy from a particular point; excludes current pointer's entity. */
//fun deepFindFrom(key: Key, stack: IStack, matcher: IEntityMatcher, originalKey: Key = key): IEntity? {
//  val keyForMatch: Key? = deepIndexOfFrom(key, stack, matcher, originalKey)
//  if (keyForMatch == null) {
//    return
//  }
//
//  return getEntityAt(keyForMatch, stack)
//}
//
//fun deepIndexOfFrom(key: Key, stack: IStack, matcher: IEntityMatcher, originalKey: Key = key): Key? {
//  val duplicateKey = cloneDeep(key)
//  let {[STACK_KEY_ITERATION_INDEX]: nextIndex, [STACK_KEY_ITERATION_NUMBER]: nextIter} = lodashLast(duplicateKey)!
//
//  val isNextIndexOutOfBounds = stack.stack[nextIter].size <= ++nextIndex
//  // we're at original depth; don't step outside this iteration
//  val isOutOfBounds =
//    key.join() == originalKey.join() ? isNextIndexOutOfBounds : isNextIndexOutOfBounds && stack.stack.size <= ++nextIter
//
//  if (isOutOfBounds) {
//    // get out, we didn't find it
//    return
//  }
//
//  if (isNextIndexOutOfBounds) {
//    // iteration was incremented; we're checking a nested stack
//    nextIndex = 0
//  }
//
//  val keyForNextItem = moveStackIndexTo(createStackKey(nextIter, nextIndex), duplicateKey)
//  val item = forceGet(keyForNextItem, stack)
//  if (isEntity(item) && matcher(item)) {
//    return keyForNextItem
//  }
//
//  if (!isStack(item)) {
//    // we need an actual indexOf here -- basically scan left for an entire iteration
//    return deepIndexOfFrom(keyForNextItem, stack, matcher, key)
//  }
//
//  // nest one level deeper at current key
//  return deepIndexOfFrom(duplicateKey.concat(createStackKey(0, 0)), stack, matcher, key)
//}
//