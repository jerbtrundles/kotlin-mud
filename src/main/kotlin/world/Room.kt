package world

import Inventory
import entity.EntityBase
import game.Game
import game.GameActionType
import java.util.UUID

open class Room(
    val id: Int,
    val coordinates: WorldCoordinates,
    val description: String,
    val connections: List<Connection>,
    val inventory: Inventory = Inventory(),
    val entities: MutableList<EntityBase> = mutableListOf()
) {
    private val uuid = UUID.randomUUID()!!

    private val directionalExitsString = "Obvious exits: " +
            connections.filter { connection ->
                connection.matchInput.action == GameActionType.MOVE
            }.joinToString { connection ->
                connection.matchInput.suffix
            }

    private val entitiesString: String
        get() = if (entities.isEmpty()) {
            ""
        } else {
            "You also see " +
                    Common.collectionString(
                        entities.map { entity -> entity.nameForCollectionString }
                    ) + ".\n"
        }

    private val inventoryString: String
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
            Game.println(str)
        }
    }

    override fun equals(other: Any?) = (uuid == (other as Room).uuid)
    override fun hashCode(): Int = uuid.hashCode()
}