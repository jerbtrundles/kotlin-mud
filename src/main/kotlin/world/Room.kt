package world

import Inventory
import entity.EntityFriendlyNpc
import entity.EntityMonster
import game.Game
import game.GameActionType
import java.util.UUID

open class Room(
    val id: Int,
    val coordinates: WorldCoordinates,
    val description: String,
    val connections: List<Connection>,
    val inventory: Inventory = Inventory(),
    val monsters: MutableList<EntityMonster> = mutableListOf(),
    val npcs: MutableList<EntityFriendlyNpc> = mutableListOf()
) {
    private val uuid = UUID.randomUUID()!!

    private val directionalExitsString = "Obvious exits: " +
            connections.filter { connection ->
                connection.matchInput.action == GameActionType.MOVE
            }.joinToString { connection ->
                connection.matchInput.suffix
            }

    private val npcsString: String
        get() = if(npcs.isEmpty()) {
            ""
        } else {
            "You also see " +
                    Common.collectionString(
                        itemStrings = npcs.map { npc -> npc.nameForCollectionString },
                        includeIndefiniteArticles = false
                    ) + ".\n"
        }

    private val monstersString: String
        get() = if (monsters.isEmpty()) {
            ""
        } else {
            "You also see " +
                    Common.collectionString(
                        monsters.map { monster -> monster.nameForCollectionString }
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
        sb.append(npcsString)
        sb.append(monstersString)
        sb.append(directionalExitsString)
        return sb.toString()
    }

    fun addMonster(monster: EntityMonster) {
        monsters.add(monster)
        announce(monster.arriveString)
    }

    fun addNpc(npc: EntityFriendlyNpc) {
        npcs.add(npc)
        announce(npc.arriveString)
    }

    fun announce(str: String) {
        if (Player.currentRoom == this) {
            Game.println(str)
        }
    }

    override fun equals(other: Any?) = (uuid == (other as Room).uuid)
    override fun hashCode(): Int = uuid.hashCode()
}