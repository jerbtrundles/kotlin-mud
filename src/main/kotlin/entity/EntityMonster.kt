package entity

import debug.Debug
import entity.behavior.EntityAction
import entity.behavior.EntitySituation
import game.Game
import game.MovementDirection
import withIndefiniteArticle
import world.Connection
import world.Room
import world.World

class EntityMonster(
    name: String,
    keywords: List<String>,
    // level and attributes remain constant
    val level: Int,
    attributes: EntityAttributes,
    val experience: Int,
    val gold: Int
) : EntityBase(name, keywords, attributes) {

    override val nameForStory = "The $name"
    override val nameForCollectionString
        get() = if (isDead) {
            "dead $name"
        } else {
            name
        }

    override val arriveString = "${name.withIndefiniteArticle(capitalized = true)} has arrived."
    override fun departString(connection: Connection): String {
        return if (connection.direction != MovementDirection.NONE) {
            // The goblin heads east.
            "The $name heads ${connection.direction.toString().lowercase()}."
        } else if (connection.matchInputString.contains("gates")) {
            // TODO: make this better
            // TODO: other cases for climbing, other connection types
            // TODO: The goblin heads through the gates.
            "The $name heads through the town gates."
        } else {
            // TODO: make this better
            "The $name heads over to the ${connection.matchInput.suffix}."
        }
    }

    override val deathString = "The $name dies."

    override suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        doInit(initialRoom)

        while (hasNotBeenSearched && Game.running) {
            doDelay()
            doAction()
        }

        Debug.println("EntityMonster::goLiveYourLifeAndBeFree() - $name is dead, has been searched, and will now decay")
        doDecay()
    }

    override fun doInit(initialRoom: Room) {
        // set initial room and add self
        currentRoom = initialRoom
        currentRoom.addMonster(this)

        Debug.println("EntityMonster::doInit() - adding ${this.name} to ${currentRoom.coordinates}")
    }

    override fun doAction() {
        if (isDead) {
            return
        }

        val action = behavior.getNextAction(this)
        Debug.println("EntityMonster::doAction() - $action")

        when(action) {
            EntityAction.MOVE -> doRandomMove()
            EntityAction.SIT -> doSit()
            EntityAction.STAND -> doStand()
            EntityAction.GET_WEAPON -> doGetRandomWeapon()
            EntityAction.GET_ARMOR -> doGetRandomArmor()
            else -> doNothing()
        }
    }

    private fun doGetRandomArmor() {
        currentRoom.inventory.getRandomArmor()?.let {new ->
            armor?.let { old ->
                currentRoom.announce("The $name drops ${old.nameWithIndefiniteArticle}.")
                currentRoom.inventory.items.add(old)
            }

            armor = new
            currentRoom.announce("The $name picks up ${new.nameWithIndefiniteArticle}.")
        } ?: {
            Debug.println("EntityMonster::doGetRandomArmor() - no armor in current room")
            doNothing()
        }
    }

    private fun doGetRandomWeapon() {
        currentRoom.inventory.getRandomWeapon()?.let {new ->
            weapon?.let { old ->
                currentRoom.announce("The $name drops ${old.nameWithIndefiniteArticle}.")
                currentRoom.inventory.items.add(old)
            }

            weapon = new
            currentRoom.announce("The $name picks up ${new.nameWithIndefiniteArticle}.")
        } ?: {
            Debug.println("EntityMonster::doGetRandomWeapon() - no weapon in current room")
            doNothing()
        }
    }

    private fun doSit() {
        if(posture != EntityPosture.SITTING) {
            posture = EntityPosture.SITTING
            currentRoom.announce("The $name sits down.")
        } else {
            Debug.println("EntityMonster::doSit() - already sitting")
            doNothing()
        }
    }

    private fun doNothing() {
        Debug.println("EntityMonster::doNothing()")
    }

    private fun doStand() {
        if(posture != EntityPosture.STANDING) {
            Debug.println("EntityMonster::doStand()")
            posture = EntityPosture.STANDING
            currentRoom.announce("The $name stands up.")
        } else {
            Debug.println("EntityMonster::doStand() - already standing")
            doNothing()
        }
    }

    override fun doRandomMove() {
        if(posture != EntityPosture.STANDING) {
            Debug.println("EntityMonster::doRandomMove() - need to stand")
            doStand()
        } else {
            val connection = currentRoom.connections.random()
            val newRoom = World.getRoomFromCoordinates(connection.coordinates)
            doMove(newRoom, connection)
        }
    }

    private fun doMove(newRoom: Room, connection: Connection) {
        // debug.Debug.println("EntityMonster::doRandomMove() - ${this.name} - move from ${currentRoom.coordinates} to ${newRoom.coordinates}")

        // leaving
        currentRoom.monsters.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving (addMonster handles announce)
        currentRoom.addMonster(this)
    }

    fun assessSituations() {
        EntitySituation.values().forEach { situation ->
            Game.println("$situation: ${isInSituation(situation)}")
        }
    }
}