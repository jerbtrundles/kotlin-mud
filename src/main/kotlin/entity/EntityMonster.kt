package entity

import game.Game
import game.MovementDirection
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
    attributes: EntityAttributes,
    val experience: Int,
    val gold: Int
) : EntityBase(name, keywords, attributes) {
    val hasBeenSearched
        // TODO: make this false when done debugging
        get() = isDead // = false

    override val nameForCollectionString
        get() = if (isDead) {
            "dead $name"
        } else {
            name
        }

    override val arriveString = "${name.withIndefiniteArticle(capitalized = true)} has arrived."
    override fun departString(connection: Connection): String {
        return if(connection.direction != MovementDirection.NONE) {
            // The goblin heads east.
            "The $name heads ${connection.direction.toString().lowercase()}."
        } else {
            // TODO: The goblin heads through the gates.
            "The $name heads over to the ${connection.matchInput.suffix}."
        }
    }

    override val deathString = "The $name dies."

    override suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        currentRoom = initialRoom
        currentRoom.addMonster(this)

        while (!hasBeenSearched && Game.running) {
            // TODO: currently hard-coded to wait x-y seconds (e.g. 5s-10s -> 50cs*100 - 100cs*100)
            //  make this based off of something else
            //  e.g. entity speed, type
            doDelay()

            if (!isDead) {
                doAction()
            }
        }

        if (Game.running) {
            // decay event
            currentRoom.announce("The body of the $name crumbles to dust.")
        }
    }

    private suspend fun doDelay() {
        val repeat = Random.nextInt(
            Debug.monsterDelayMin / 100,
            Debug.monsterDelayMax / 100
        )
        repeat(repeat) {
            if (Game.running && !hasBeenSearched) {
                delay(100)
            }
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