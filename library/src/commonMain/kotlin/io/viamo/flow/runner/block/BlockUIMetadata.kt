package io.viamo.flow.runner.block

import kotlinx.serialization.Serializable

/**
 * A set of key-value records describing information about how blocks are displayed on a UI/flowchart editor
 */
interface IBlockUIMetadata {
  val canvas_coordinates: ICoordinates
}

/**
 * Coordinates indicating location of this block on the Flow Builder's canvas
 */
interface ICoordinates {
  val x: Number
  val y: Number
}

@Serializable
data class BlockUIMetadata(override val canvas_coordinates: Coordinates) : IBlockUIMetadata

@Serializable
data class Coordinates(override val x: Int, override val y: Int) : ICoordinates




