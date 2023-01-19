package world

import Inventory
import com.beust.klaxon.Json
import entity.EntityBase
import game.GameActionType

class Room(
    @Json(name = "room-id")
    val id: Int,
    @Json(name = "room-description")
    val description: String,
    @Json(name = "room-connections")
    val connections: List<Connection>,
    @Json(ignored = true)
    val inventory: Inventory = Inventory(),
    @Json(ignored = true)
    val entities: MutableList<EntityBase> = mutableListOf()
) {
    private val directionalExitsString = "Obvious exits: " +
            connections.filter { connection -> connection.matchInput.action == GameActionType.MOVE }
                .joinToString {
                    // TODO: don't read words[1] directly; create suffix instead
                        connection ->
                    connection.matchInput.words[1]
                }

    val entitiesString: String
        get() = if (entities.isEmpty()) {
            ""
        } else {
            Common.collectionString(
                entities.map { entity -> entity.name }
            ) + "\n"
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
        sb.append(directionalExitsString)
        return sb.toString()
    }
}