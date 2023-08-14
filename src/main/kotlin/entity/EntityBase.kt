package entity

import Inventory
import debug.Debug
import entity.behavior.EntityBehavior
import entity.behavior.EntitySituation
import game.Game
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

    val behavior = EntityBehavior.default

    val coordinates
        get() = currentRoom.coordinates

    abstract val nameForCollectionString: String
    open val nameForStory = name
    abstract val arriveString: String
    abstract val deathString: String

    val isDead
        get() = attributes.currentHealth <= 0

    var hasNotBeenSearched = true

//        // TODO: make this false when done debugging
//        get() = false // isDead // = false

    abstract fun departString(connection: Connection): String
    abstract suspend fun goLiveYourLifeAndBeFree(initialRoom: Room)
    abstract fun doRandomMove()
    abstract fun doInit(initialRoom: Room)
    suspend fun doDelay() {
        // debug.Debug.println("EntityBase::doDelay()")
        // TODO: just using one range for all entities right now; might not be good enough
        Game.delayRandom(
            min = Debug.monsterDelayMin, max = Debug.monsterDelayMax,
            conditions = listOf(
                ::hasNotBeenSearched
            )
        )
    }

    abstract fun doAction()
    protected fun doDecay() {
        // TODO: currently hard-coded to wait x-y seconds (e.g. 5s-10s -> 50cs*100 - 100cs*100)
        //  make this based off of something else
        //  e.g. entity speed, type
        currentRoom.announce("The body of the $name crumbles to dust.")
    }

    fun takeDamage(damage: Int) {
        attributes.currentHealth -= damage
        if (attributes.currentHealth <= 0) {
            currentRoom.announce(deathString)
        }
    }

    fun isInSituation(situation: EntitySituation): Boolean {
        return when (situation) {
            EntitySituation.INJURED_MINOR -> attributes.isInjuredMinor()
            EntitySituation.INJURED_MODERATE -> attributes.isInjuredModerate()
            EntitySituation.INJURED_MAJOR -> attributes.isInjuredMajor()
            EntitySituation.SITTING -> posture == EntityPosture.SITTING
            EntitySituation.NOT_SITTING -> posture != EntityPosture.SITTING
            EntitySituation.STANDING -> posture == EntityPosture.STANDING
            EntitySituation.KNEELING -> posture == EntityPosture.KNEELING

            EntitySituation.ALONE -> currentRoom.npcs.isEmpty()
                    && currentRoom.monsters.size == 1               // self
                    && Player.currentRoom != currentRoom

            EntitySituation.NOT_ALONE -> !isInSituation(EntitySituation.ALONE)
            EntitySituation.SAME_ROOM_AS_PLAYER -> currentRoom == Player.currentRoom

            EntitySituation.NO_NPCS -> currentRoom.npcs.isEmpty()
            EntitySituation.SINGLE_NPC -> currentRoom.npcs.size == 1
            EntitySituation.MULTIPLE_NPCS -> currentRoom.npcs.size > 1
            EntitySituation.NO_HOSTILES -> currentRoom.npcs.isEmpty()
            EntitySituation.SINGLE_HOSTILE -> currentRoom.npcs.size == 1
            EntitySituation.MULTIPLE_HOSTILES -> currentRoom.npcs.size > 1

            EntitySituation.FOUND_GOOD_ARMOR -> false
            EntitySituation.FOUND_GOOD_ITEM -> false

            EntitySituation.WITH_OTHER_MONSTER -> currentRoom.monsters.size > 1
            EntitySituation.FOUND_GOOD_WEAPON -> false
            EntitySituation.WITH_OTHER_MONSTER_SAME_TYPE -> false
            EntitySituation.WITH_PACK -> false
            EntitySituation.WITH_PACK_SAME_TYPE -> false
            EntitySituation.NORMAL -> false

            EntitySituation.HAS_WEAPON_EQUIPPED -> weapon != null
            EntitySituation.FOUND_BETTER_ARMOR -> foundBetterArmor()
            EntitySituation.FOUND_BETTER_WEAPON -> foundBetterWeapon()

            else -> false
        }
    }

    private fun foundBetterArmor() =
        // if we find armor in the current room...
        currentRoom.inventory.getBestArmor()?.let { bestArmor ->
            // if we already have armor equipped...
            armor?.let {
                // return whether my defense is less than best-in-room
                it.defense < bestArmor.defense
                // found armor, and i have none equipped
            } ?: true
            // didn't find armor
        } ?: false

    private fun foundBetterWeapon() =
        currentRoom.inventory.getBestWeapon()?.let { bestWeapon ->
            weapon?.let {
                it.power < bestWeapon.power
                // found a weapon, and i have nothing equipped
            } ?: true
            // didn't find a weapon
        } ?: false
}