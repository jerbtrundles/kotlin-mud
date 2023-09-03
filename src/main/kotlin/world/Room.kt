package world

import Inventory
import entity.EntityFriendlyNpc
import entity.EntityMonster
import game.Game
import game.GameActionType
import item.ItemWeapon
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
        get() = if (npcs.isEmpty()) {
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
            "You also see ${inventory.collectionString}.\n"
        }

    override fun toString() = "Room: $coordinates"

    fun displayString(): String {
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

    fun randomLivingMonsterOrNull() = monsters.filter { !it.isDead }.randomOrNull()
    fun randomLivingNpcOrNull() = npcs.filter { !it.isDead }.randomOrNull()

    fun findLivingMonster(keyword: String) = monsters.firstOrNull { entity -> entity.matchesKeyword(keyword) && !entity.isDead }


    fun findDeadMonster(keyword: String): EntityMonster? =
        monsters.firstOrNull { monster ->
            monster.isDead
                    && monster.hasNotBeenSearched
                    && (monster.keywords.contains(keyword)
                    || monster.name == keyword)
        }

    fun findAnyMonster(keyword: String): EntityMonster? =
        monsters.firstOrNull { monster ->
            monster.keywords.contains(keyword)
                    || monster.name == keyword
        }

    val containsWeapon
        get() = inventory.items.any { it is ItemWeapon }

    override fun equals(other: Any?) = (uuid == (other as? Room)?.uuid)
    override fun hashCode() = uuid.hashCode()
    fun findDeadAndUnsearchedMonster(suffix: String) =
        monsters.firstOrNull { it.matchesKeyword(suffix) && it.isDead && it.hasNotBeenSearched }

}

// find an item, item comes with fluff text, maybe a story
// one-liners vs story
// story has a collection of strings, play one string at a time, move to next, message cooldown, repeat until done
// story cooldown; don't play the same story over and over too quickly