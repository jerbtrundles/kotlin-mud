package entity

import item.ItemBase
import world.World
import world.WorldCoordinates

abstract class EntityBase(
    // level and attributes remain constant
    val level: Int,
    val attributes: EntityAttributes,
    // entities can move
    var coordinates: WorldCoordinates,
    val inventory: EntityInventory,
    // TODO: should all entities have two hands?
    var leftHand: ItemBase? = null,
    var rightHand: ItemBase? = null
) {
    var posture = EntityPosture.STANDING
    val currentRoom
        get() = World.getRoomFromCoordinates(coordinates)

    val isDead
        get() = attributes.currentHealth <= 0

    var hasBeenSearched = false
}