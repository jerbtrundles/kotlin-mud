package entity

import game.Game
import kotlinx.coroutines.delay
import withIndefiniteArticle
import world.Connection
import world.Room
import world.World
import kotlin.random.Random

class EntityBase(
    // level and attributes remain constant
    val level: Int,
    val name: String,
    val experience: Int,
    val gold: Int,
    val attributes: EntityAttributes,
    val inventory: EntityInventory,
    var currentRoom: Room = World.zero,
    val keywords: List<String>
) {
    val nameForCollectionString
        get() = if(isDead) {
            "dead $name"
        } else {
            name
        }

    var posture: EntityPosture = EntityPosture.STANDING
    val arriveString = "${name.withIndefiniteArticle(capitalized = true)} has arrived."
    fun departString(connection: Connection): String {
        // The goblin heads east.
        return "The $name heads ${connection.direction.toString().lowercase()}."

        // TODO: The goblin heads through the gates.
    }

    val coordinates
        get() = currentRoom.coordinates

    val isDead
        get() = attributes.currentHealth <= 0

    var hasBeenSearched = false

    suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        initialRoom.addEntity(this)

        while (!hasBeenSearched && Game.running) {
            val repeat = Random.nextInt(20, 50)
            repeat(repeat) {
                if (Game.running && !hasBeenSearched) {
                    delay(100)
                }
            }

            if (!isDead) {
                doAction()
            }
        }

        if (Game.running) {
            currentRoom.announce("The body of the $name crumbles to dust.")
        }
    }

    private fun doAction() {
        doRandomMove()

        /*when (Random.nextInt(2)) {
            0 -> currentRoom.announce("\"Mrrrrrrr...\" says the $name.")
            1 -> doRandomMove()
            else -> {
                Game.print("EIHGHIEGOWSJDSOIJF")
            }
        }*/
    }

    private fun doRandomMove() {
        val connection = currentRoom.connections.random()
        val newRoom = World.getRoomFromCoordinates(connection.coordinates)

        // leaving
        currentRoom.entities.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving
        currentRoom.addEntity(this)

        // if directional
        // otherwise (The goblin goes through the gates.)
    }
}