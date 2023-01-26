package entity

import game.Game
import world.Connection
import world.Room
import world.World
import kotlin.random.Random

class EntityFriendlyNpc(
    name: String,
    val job: String
) : EntityBase(
    name = name,
    keywords = listOf(name)
) {
    val nameWithJob = "$name the $job"
    val randomName
        get() = if (Random.nextInt(0, 2) == 0) {
            nameWithJob
        } else {
            name
        }

    override val arriveString = "$nameWithJob walks in."
    override fun departString(connection: Connection): String {
        return "$nameWithJob heads ${connection.direction.toString().lowercase()}."
    }

    override val nameForCollectionString
        get() = nameWithJob

    override suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        currentRoom = initialRoom
        initialRoom.addNpc(this)

        while (Game.running) {
            // TODO: make this based off of something else
            //  e.g. entity speed, type
            Common.delayRandom(from = 500, to = 1000)
            doAction()
        }
    }

    override fun doAction() {
        when (Random.nextInt(5)) {
            0 -> doGetRandomItemFromRoom()
            1 -> doRandomMove()
            2 -> doExchangeWordsWithNpc()
            else -> doDropRandomItemFromInventory()
//            2 -> currentRoom.announce("$randomName shuffles their feet.")
//            3 -> currentRoom.announce("$randomName gazes at the sky.")
//            4 -> currentRoom.announce("$randomName glances around.")
//            5 -> currentRoom.announce("$randomName says \"Mrrrrrrr...\"")
        }
    }

    private fun doExchangeWordsWithNpc() {
        val npc = currentRoom.npcs.random()
        if (npc == this) {
            currentRoom.announce("$name mumbles something to themselves.")
        } else {
            currentRoom.announce("$randomName exchanges a few words with ${npc.randomName}.")
        }
    }

    private fun doGetRandomItemFromRoom() {
        currentRoom.inventory.items.randomOrNull()?.let { item ->
            inventory.items.add(item)
            currentRoom.inventory.items.remove(item)

            currentRoom.announce("$randomName picks up ${item.nameWithIndefiniteArticle}.")
        }
    }

    private fun doDropRandomItemFromInventory() {
        inventory.items.randomOrNull()?.let { item ->
            currentRoom.inventory.items.add(item)
            inventory.items.remove(item)

            currentRoom.announce("$randomName drops ${item.nameWithIndefiniteArticle}.")
        }
    }

    override fun doRandomMove() {
        val connection = currentRoom.connections.random()
        val newRoom = World.getRoomFromCoordinates(connection.coordinates)

        // leaving
        currentRoom.npcs.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving
        currentRoom.addNpc(this)

        // if directional
        // otherwise (The goblin goes through the gates.)
    }
}