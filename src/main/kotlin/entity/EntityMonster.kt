package entity

import game.Game
import kotlinx.coroutines.delay
import withIndefiniteArticle
import world.Connection
import world.Room
import world.World
import kotlin.random.Random

class EntityMonster(
    name: String,
    keywords: List<String>,
    // level and attributes remain constant
    val level: Int,
    val attributes: EntityAttributes,
    val experience: Int,
    val gold: Int
) : EntityBase(name, keywords) {

    var hasBeenSearched = false
    val isDead
        get() = attributes.currentHealth <= 0

    override val nameForCollectionString
        get() = if (isDead) {
            "dead $name"
        } else {
            name
        }

    override val arriveString = "${name.withIndefiniteArticle(capitalized = true)} has arrived."
    override fun departString(connection: Connection): String {
        // The goblin heads east.
        return "The $name heads ${connection.direction.toString().lowercase()}."

        // TODO: The goblin heads through the gates.
    }

    override suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        currentRoom = initialRoom
        currentRoom.addMonster(this)

        while (!hasBeenSearched && Game.running) {
            // TODO: currently hard-coded to wait 2-5 seconds
            //  make this based off of something else
            //  e.g. entity speed, type
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
            // decay event
            currentRoom.announce("The body of the $name crumbles to dust.")
        }
    }

    override fun doAction() {
        doRandomMove()

        /*when (Random.nextInt(2)) {
            0 -> currentRoom.announce("\"Mrrrrrrr...\" says the $name.")
            1 -> doRandomMove()
            else -> {
                Game.print("EIHGHIEGOWSJDSOIJF")
            }
        }*/
    }

    override fun doRandomMove() {
        val connection = currentRoom.connections.random()
        val newRoom = World.getRoomFromCoordinates(connection.coordinates)

        // leaving
        currentRoom.monsters.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving
        currentRoom.addMonster(this)

        // if directional
        // otherwise (The goblin goes through the gates.)
    }
}