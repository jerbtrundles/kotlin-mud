package entity

import item.ItemBase
import world.World
import world.WorldCoordinates
import javax.swing.text.html.parser.Entity

class EntityBase(
    // level and attributes remain constant
    val level: Int,
    val name: String,
    val attributes: EntityAttributes,
    // entities can move
    var coordinates: WorldCoordinates,
    val inventory: EntityInventory
) {
    var posture = EntityPosture.STANDING
    val currentRoom
        get() = World.getRoomFromCoordinates(coordinates)

    val isDead
        get() = attributes.currentHealth <= 0

    var hasBeenSearched = false

    fun cloneAtCoordinates(
        coordinates: WorldCoordinates
    ): EntityBase {
        return EntityBase(
            level = this.level,
            name = this.name,
            attributes = this.attributes,
            coordinates = coordinates,
            inventory = this.inventory,
        )
    }
}