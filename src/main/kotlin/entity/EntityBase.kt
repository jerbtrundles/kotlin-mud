package entity

import world.Connection
import world.Room
import world.World

abstract class EntityBase(
    val name: String,
    val keywords: List<String>
) {
    val inventory: EntityInventory = EntityInventory()
    var currentRoom: Room = World.void
    var posture: EntityPosture = EntityPosture.STANDING

    val coordinates
        get() = currentRoom.coordinates

    abstract val nameForCollectionString: String
    abstract val arriveString: String
    abstract fun departString(connection: Connection): String
    abstract suspend fun goLiveYourLifeAndBeFree(initialRoom: Room)
    abstract fun doAction()
    abstract fun doRandomMove()

}