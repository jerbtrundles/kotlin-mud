package entity

import Inventory
import item.ItemArmor
import item.ItemWeapon
import world.Connection
import world.Room
import world.World

abstract class EntityBase(
    val name: String,
    val keywords: List<String>,
    val attributes: EntityAttributes
) {
    val inventory: Inventory = Inventory()
    var currentRoom: Room = World.void
    var posture: EntityPosture = EntityPosture.STANDING
    var weapon: ItemWeapon? = null
    var armor: ItemArmor? = null

    val coordinates
        get() = currentRoom.coordinates

    abstract val nameForCollectionString: String
    abstract val arriveString: String
    abstract val deathString: String

    val isDead
        get() = attributes.currentHealth <= 0

    abstract fun departString(connection: Connection): String
    abstract suspend fun goLiveYourLifeAndBeFree(initialRoom: Room)
    abstract fun doAction()
    abstract fun doRandomMove()

    fun takeDamage(damage: Int) {
        attributes.currentHealth -= damage
        if (attributes.currentHealth <= 0) {
            currentRoom.announce(deathString)
        }
    }
}