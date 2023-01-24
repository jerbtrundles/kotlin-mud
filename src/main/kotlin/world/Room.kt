package world

import Inventory
import com.beust.klaxon.Json
import entity.EntityBase
import game.Game
import game.GameActionType
import java.util.UUID

class Room(
    @Json(name = "room-id")
    val id: Int,
    @Json(name = "room-coordinates")
    val coordinatesString: String,
    @Json(name = "room-description")
    val description: String,
    @Json(name = "room-connections")
    val connections: List<Connection>,
    @Json(ignored = true)
    val inventory: Inventory = Inventory(),
    @Json(ignored = true)
    val entities: MutableList<EntityBase> = mutableListOf()
) {
    val uuid = UUID.randomUUID()
    val coordinates = WorldCoordinates.parseFromString(coordinatesString)

    private val directionalExitsString = "Obvious exits: " +
            connections.filter { connection ->
                connection.matchInput.action == GameActionType.MOVE
            }.joinToString { connection ->
                connection.matchInput.suffix
            }

    val entitiesString: String
        get() = if (entities.isEmpty()) {
            ""
        } else {
            "You also see " +
                    Common.collectionString(
                        entities.map { entity -> entity.nameForCollectionString }
                    ) + ".\n"
        }

    val inventoryString: String
        get() = if (inventory.items.isEmpty()) {
            ""
        } else {
            "You also see $inventory.\n"
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine(description)
        sb.append(inventoryString)
        sb.append(entitiesString)
        sb.append(directionalExitsString)
        return sb.toString()
    }

    fun addEntity(entity: EntityBase) {
        entities.add(entity)
        announce(entity.arriveString)
    }

    fun announce(str: String) {
        if (Player.currentRoom == this) {
            Game.println("${coordinatesString} - $str")
        }
    }

    override fun equals(other: Any?) = (uuid == (other as Room).uuid)
}