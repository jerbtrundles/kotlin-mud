package entity

import Inventory
import debug.Debug
import entity.behavior.EntityAction
import entity.behavior.EntityBehavior
import entity.behavior.EntitySituation
import game.Game
import item.ItemArmor
import item.ItemBase
import item.ItemWeapon
import world.Connection
import world.Room
import world.World

abstract class EntityBase(
    val name: String,
    var level: Int,
    val keywords: List<String>,
    val attributes: EntityAttributes
) {
    val inventory: Inventory = Inventory()
    var currentRoom: Room = World.void
    protected var posture: EntityPosture = EntityPosture.STANDING
    var weapon: ItemWeapon? = null
    var armor: ItemArmor? = null

    private val behavior = EntityBehavior.default

    val coordinates
        get() = currentRoom.coordinates

    abstract val randomName: String
    abstract val nameForCollectionString: String
    open val nameForStory = name
    abstract val arriveString: String
    abstract val deathString: String
    abstract val sitString: String
    abstract val standString: String
    abstract val kneelString: String
    abstract fun putAwayString(item: ItemBase): String
    abstract fun equipString(item: ItemBase): String
    abstract fun removeString(item: ItemBase): String
    val isDead
        get() = attributes.currentHealth <= 0

    var hasNotBeenSearched = true
    abstract val hostilesCount: Int
    abstract val deadAndUnsearchedHostilesCount: Int

//        // TODO: make this false when done debugging
//        get() = false // isDead // = false

    abstract fun departString(connection: Connection): String
    suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        doInit(initialRoom)

        while (hasNotBeenSearched && Game.running) {
            doDelay()
            doAction()
        }

        doFinalCleanup()
    }

    fun matchesKeyword(keyword: String) = (name == keyword) || keywords.contains(keyword)

    private fun doRandomMove() {
        if (posture != EntityPosture.STANDING) {
            Debug.println("EntityBase::doRandomMove() - need to stand")
            doStand()
        } else {
            val connection = currentRoom.connections.random()
            val newRoom = World.getRoomFromCoordinates(connection.coordinates)
            doMove(newRoom, connection)
        }
    }

    abstract fun doMove(newRoom: Room, connection: Connection)
    abstract fun doInit(initialRoom: Room)
    abstract suspend fun doDelay()

    abstract fun doIdle()
    abstract fun isAlone(): Boolean
    fun isInSituation(situation: EntitySituation): Boolean {
        return when (situation) {
            EntitySituation.INJURED_MINOR -> attributes.isInjuredMinor()
            EntitySituation.INJURED_MODERATE -> attributes.isInjuredModerate()
            EntitySituation.INJURED_MAJOR -> attributes.isInjuredMajor()
            EntitySituation.SITTING -> posture == EntityPosture.SITTING
            EntitySituation.NOT_SITTING -> posture != EntityPosture.SITTING
            EntitySituation.STANDING -> posture == EntityPosture.STANDING
            EntitySituation.KNEELING -> posture == EntityPosture.KNEELING

            EntitySituation.ALONE -> isAlone()
            EntitySituation.NOT_ALONE -> !isAlone()

            EntitySituation.SAME_ROOM_AS_PLAYER -> currentRoom == Player.currentRoom

            EntitySituation.NO_MONSTERS -> currentRoom.monsters.isEmpty()
            EntitySituation.SINGLE_MONSTER -> currentRoom.monsters.size == 1
            EntitySituation.MULTIPLE_MONSTERS -> currentRoom.monsters.size > 1

            EntitySituation.NO_NPCS -> currentRoom.npcs.isEmpty()
            EntitySituation.SINGLE_NPC -> currentRoom.npcs.size == 1
            EntitySituation.MULTIPLE_NPCS -> currentRoom.npcs.size > 1

            EntitySituation.NO_HOSTILES -> hostilesCount == 0
            EntitySituation.SINGLE_HOSTILE -> hostilesCount == 1
            EntitySituation.MULTIPLE_HOSTILES -> hostilesCount > 1
            EntitySituation.ANY_HOSTILES -> hostilesCount > 0

            EntitySituation.FOUND_GOOD_ARMOR -> false
            EntitySituation.FOUND_GOOD_ITEM -> false

            EntitySituation.FOUND_GOOD_WEAPON -> false
            EntitySituation.WITH_OTHER_MONSTER_SAME_TYPE -> false
            EntitySituation.WITH_PACK -> false
            EntitySituation.WITH_PACK_SAME_TYPE -> false
            EntitySituation.NORMAL -> false

            EntitySituation.HAS_WEAPON_EQUIPPED -> weapon != null
            EntitySituation.FOUND_BETTER_ARMOR -> foundBetterArmor()
            EntitySituation.FOUND_BETTER_WEAPON -> foundBetterWeapon()

            EntitySituation.NO_EQUIPPED_WEAPON -> weapon == null
            EntitySituation.NO_EQUIPPED_ARMOR -> armor == null

            EntitySituation.ANY_UNSEARCHED_DEAD_HOSTILES -> deadAndUnsearchedHostilesCount > 0
            EntitySituation.CURRENT_ROOM_CONTAINS_WEAPON -> currentRoom.containsWeapon
            EntitySituation.ANY -> true
            else -> false
        }
    }

    abstract fun doMumble()
    abstract fun calculateAttackPower(): Int


    protected fun doAction() {
        if (isDead) {
            return
        }

        val action = behavior.getNextAction(this)
        Debug.println("EntityBase::doAction() - $name - $action")

        when (action) {
            EntityAction.MOVE -> doRandomMove()
            EntityAction.SIT -> doSit()
            EntityAction.STAND -> doStand()
            EntityAction.KNEEL -> doKneel()
            EntityAction.CHATTER -> doChatter()
            EntityAction.GET_RANDOM_BETTER_WEAPON -> doGetRandomBetterWeapon()
            EntityAction.GET_RANDOM_BETTER_ARMOR -> doGetRandomBetterArmor()
            EntityAction.GET_RANDOM_ITEM -> doGetRandomItem()
            EntityAction.IDLE -> doIdle()
            EntityAction.ATTACK_PLAYER -> doAttackPlayer()
            EntityAction.ATTACK_RANDOM_HOSTILE -> doAttackRandomHostile()
            EntityAction.SEARCH_RANDOM_UNSEARCHED_DEAD_HOSTILE -> doSearchRandomUnsearchedDeadHostile()
            EntityAction.FIND_ANY_WEAPON -> doFindAnyWeapon()
            else -> doNothing()
        }
    }

    fun doFindAnyWeapon() {
        currentRoom.inventory.getAndRemoveRandomWeapon()?.let { foundWeapon ->
            if (weapon != null) {
                // drop current weapon (shouldn't ever happen right now)
            }
            weapon = foundWeapon
            currentRoom.announce(getString(foundWeapon))
        }
    }

    abstract fun doSearchRandomUnsearchedDeadHostile()
    abstract fun doChatter()

    abstract fun doAttack()
    private fun doAttackPlayer() {
        // already verified if evaluated as a situation
        // TODO: can this method be called any other way?
        //  if so, might need to add verification
    }

    abstract fun doAttackRandomHostile()

    abstract fun dropString(item: ItemBase): String
    abstract fun dropString(inventory: Inventory): String
    abstract fun getString(item: ItemBase): String
    private fun doGetRandomItem() = currentRoom.inventory.getAndRemoveRandomItem()?.let { item ->
        currentRoom.announce(getString(item))
    }

    private fun doGetRandomBetterWeapon() {
        Debug.println("EntityBase::doGetRandomBetterWeapon()")
        currentRoom.inventory.getAndRemoveRandomBetterWeapon(weapon?.power?.plus(1) ?: 0)?.let { newWeapon ->
            weapon?.let { oldWeapon ->
                currentRoom.announce(dropString(oldWeapon))
                currentRoom.inventory.items.add(oldWeapon)
            }

            weapon = newWeapon
            currentRoom.announce(getString(newWeapon))
        } ?: {
            Debug.println("EntityBase::doGetRandomWeapon() - no weapon in current room")
            doNothing()
        }
    }

    private fun doGetRandomBetterArmor() {
        currentRoom.inventory.getRandomArmorOrNull((armor?.defense?.plus(1)) ?: 0)?.let { newArmor ->
            armor?.let { oldArmor ->
                currentRoom.inventory.items.add(oldArmor)
                currentRoom.announce(dropString(oldArmor))
            }

            armor = newArmor
            currentRoom.announce(getString(newArmor))
        } ?: doNothing()

        // Debug.println("EntityBase::doGetRandomArmor() - no armor in current room")
        // doNothing()
    }

    private fun doSit() {
        if (posture != EntityPosture.SITTING) {
            posture = EntityPosture.SITTING
            currentRoom.announce(sitString)
        }
    }

    protected fun doStand() {
        if (posture != EntityPosture.STANDING) {
            posture = EntityPosture.STANDING
            currentRoom.announce(standString)
        }
    }

    private fun doKneel() {
        if (posture != EntityPosture.KNEELING) {
            posture = EntityPosture.KNEELING
            currentRoom.announce(kneelString)
        }
    }

    private fun doEquipWeapon() {
        if (weapon != null) {
            return
        }

        inventory.getRandomTypedItem<ItemWeapon>()?.let { weaponToEquip ->
            weapon = weaponToEquip
            currentRoom.announce(equipString(weaponToEquip))
        }
    }

    private fun doRemoveWeapon() {
        weapon?.let {
            currentRoom.announce(putAwayString(it))
            inventory.items.add(it)
            weapon = null
        }
    }

    private fun doEquipArmor() {
        if (armor != null) {
            return
        }

        inventory.getRandomTypedItem<ItemArmor>()?.let { armorToEquip ->
            armor = armorToEquip
            currentRoom.announce(equipString(armorToEquip))
        }
    }

    private fun doRemoveArmor() {
        armor?.let {
            currentRoom.announce(removeString(it))
            inventory.items.add(it)
            armor = null
        }
    }

    private fun doDropRandomItemFromInventory() {
        inventory.items.randomOrNull()?.let { item ->
            currentRoom.inventory.items.add(item)
            inventory.items.remove(item)

            currentRoom.announce(dropString(item))
        }
    }

    private fun doGetRandomItemFromRoom() {
        currentRoom.inventory.items.randomOrNull()?.let { item ->
            inventory.items.add(item)
            currentRoom.inventory.items.remove(item)

            currentRoom.announce(getString(item))
        }
    }

    abstract fun doSpeakWith(entity: EntityBase)

    fun takeDamage(damage: Int) {
        attributes.currentHealth -= damage
        if (attributes.currentHealth <= 0) {
            currentRoom.announce(deathString)
        }
    }

    protected fun doNothing() {
        Debug.println("EntityMonster::doNothing()")
    }

    abstract fun doFinalCleanup()

    private fun foundBetterArmor() =
        // if we find armor in the current room...
        currentRoom.inventory.getBestArmor()?.let { bestArmor ->
            // if we already have armor equipped...
            armor?.let {
                // return whether my defense is less than best-in-room
                it.defense < bestArmor.defense
                // found armor, and I have none equipped
            } ?: true
            // didn't find armor
        } ?: false

    private fun foundBetterWeapon() =
        currentRoom.inventory.getBestWeapon()?.let { bestWeapon ->
            weapon?.let {
                it.power < bestWeapon.power
                // found a weapon, and I have nothing equipped
            } ?: true
            // didn't find a weapon
        } ?: false

    fun assessSituations() {
        EntitySituation.values().forEach { situation ->
            Game.println("$situation: ${isInSituation(situation)}")
        }
    }
}