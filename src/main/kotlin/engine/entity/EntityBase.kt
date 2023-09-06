package engine.entity

import engine.Inventory
import debug.Debug
import engine.player.Player
import engine.entity.behavior.EntityAction
import engine.entity.behavior.EntityBehavior
import engine.entity.behavior.EntitySituation
import engine.entity.behavior.FlavorText
import engine.game.Game
import engine.item.ItemArmor
import engine.item.ItemBase
import engine.item.ItemWeapon
import engine.world.Connection
import engine.world.Room
import engine.world.World

abstract class EntityBase(
    val name: String,
    var level: Int,
    val keywords: List<String>,
    val attributes: EntityAttributes,
    val behavior: EntityBehavior,
    var weapon: ItemWeapon? = null,
    var armor: ItemArmor? = null,
    val stringPrefix: String = "",
    val arriveStringSuffix: String = "walks in"
) {
    val inventory: Inventory = Inventory()
    var currentRoom: Room = World.void
    protected var posture: EntityPosture = EntityPosture.STANDING

    val coordinates
        get() = currentRoom.coordinates

    abstract val randomName: String
    abstract val nameForCollectionString: String
    open val nameForStory = name

    abstract val deathName: String
    val deathString by lazy { "$stringPrefix$deathName dies." }
    val sitString
        get() = "$stringPrefix$randomName sits down."
    val standString
        get() = "$stringPrefix$randomName stands up."
    val kneelString
        get() = "$stringPrefix$randomName kneels."

    abstract val arriveName: String
    val arriveString by lazy { "$arriveName $arriveStringSuffix." }

    fun putAwayString(item: ItemBase) =
        "$stringPrefix$randomName puts away their ${item.name}."
    fun equipString(item: ItemBase) =
        "$stringPrefix$name equips ${item.nameWithIndefiniteArticle}."
    fun removeString(item: ItemBase) =
        "$stringPrefix$randomName removes their ${item.name}."
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
    fun doIdle() { }
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
            EntitySituation.PLAYER_IS_ALIVE -> Player.isAlive

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
            EntitySituation.FOUND_ANY_ITEM -> currentRoom.inventory.items.isNotEmpty()
            EntitySituation.FOUND_VALUABLE_ITEM -> currentRoom.inventory.items.any { it.value > Debug.valuableItemMinimumValue }

            EntitySituation.FOUND_GOOD_WEAPON -> false
            EntitySituation.WITH_OTHER_MONSTER_SAME_TYPE -> false
            EntitySituation.WITH_PACK -> false
            EntitySituation.WITH_PACK_SAME_TYPE -> false
            EntitySituation.NORMAL -> false

            EntitySituation.HAS_WEAPON_EQUIPPED -> weapon != null
            EntitySituation.FOUND_BETTER_ARMOR -> foundBetterArmor()
            EntitySituation.FOUND_BETTER_WEAPON -> foundBetterWeapon()

            EntitySituation.WEAPON_IN_CURRENT_ROOM -> inventory.items.any { it is ItemWeapon }
            EntitySituation.ARMOR_IN_CURRENT_ROOM -> inventory.items.any { it is ItemArmor }

            EntitySituation.NO_EQUIPPED_WEAPON -> weapon == null
            EntitySituation.NO_EQUIPPED_ARMOR -> armor == null

            EntitySituation.ANY_UNSEARCHED_DEAD_HOSTILES -> deadAndUnsearchedHostilesCount > 0
            EntitySituation.CURRENT_ROOM_CONTAINS_WEAPON -> currentRoom.containsWeapon
            EntitySituation.ANY -> true
            else -> false
        }
    }

    fun doMumble() = announce("$stringPrefix$name mumbles something to themselves.")
    abstract fun calculateAttackPower(): Int
    fun doIsDead() { }
    protected fun doAction() {
        if (isDead) {
            doIsDead()
        } else {
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
                EntityAction.FIND_AND_EQUIP_ANY_WEAPON -> doFindAndEquipAnyWeapon()
                EntityAction.FIND_AND_EQUIP_ANY_ARMOR -> doFindAndEquipAnyArmor()
                EntityAction.GET_VALUABLE_ITEM -> doGetValuableItem()
                EntityAction.GET_ANY_ITEM -> doGetRandomItem()
                else -> doNothing()
            }
        }
    }

    fun say(what: String) = announce("$stringPrefix$name says: \"$what\"")
    fun doGetValuableItem() {
        currentRoom.inventory.getAndRemoveRandomValuableItem()?.let { item ->
            say(FlavorText.get(EntityAction.GET_VALUABLE_ITEM))
            announce(getString(item))
        }
    }

    abstract fun doFindAndEquipAnyWeapon()
    abstract fun doFindAndEquipAnyArmor()

    abstract fun doSearchRandomUnsearchedDeadHostile()
    abstract fun doChatter()
    abstract fun doAttackPlayer()
    abstract fun doAttackRandomHostile()

    fun getString(item: ItemBase) =
        "$stringPrefix$name picks up ${item.nameWithIndefiniteArticle}."
    fun dropString(item: ItemBase) =
        "$stringPrefix$name drops ${item.nameWithIndefiniteArticle}."
    fun dropString(inventory: Inventory) =
        "$stringPrefix$name drops ${inventory.collectionString}."

    private fun doGetRandomItem() = currentRoom.inventory.getAndRemoveRandomItem()?.let { item ->
        announce(getString(item))
    }

    private fun doGetRandomBetterWeapon() {
        Debug.println("EntityBase::doGetRandomBetterWeapon()")
        currentRoom.inventory.getAndRemoveRandomBetterWeaponOrNull(weapon?.power?.plus(1) ?: 0)?.let { newWeapon ->
            weapon?.let { oldWeapon ->
                announce(dropString(oldWeapon))
                currentRoom.inventory.items.add(oldWeapon)
            }

            weapon = newWeapon
            announce(getString(newWeapon))
        } ?: {
            Debug.println("EntityBase::doGetRandomWeapon() - no weapon in current room")
            doNothing()
        }
    }


    private fun doGetRandomBetterArmor() {
        currentRoom.inventory.getAndRemoveRandomBetterArmorOrNull((armor?.defense?.plus(1)) ?: 0)?.let { newArmor ->
            armor?.let { oldArmor ->
                currentRoom.inventory.items.add(oldArmor)
                announce(dropString(oldArmor))
            }

            armor = newArmor
            announce(getString(newArmor))
        } ?: doNothing()

        // Debug.println("EntityBase::doGetRandomArmor() - no armor in current room")
        // doNothing()
    }

    private fun doSit() {
        if (posture != EntityPosture.SITTING) {
            posture = EntityPosture.SITTING
            announce(sitString)
        }
    }

    protected fun doStand() {
        if (posture != EntityPosture.STANDING) {
            posture = EntityPosture.STANDING
            announce(standString)
        }
    }

    private fun doKneel() {
        if (posture != EntityPosture.KNEELING) {
            posture = EntityPosture.KNEELING
            announce(kneelString)
        }
    }

    private fun doGetRandomItemFromRoom() {
        currentRoom.inventory.items.randomOrNull()?.let { item ->
            inventory.items.add(item)
            currentRoom.inventory.items.remove(item)

            announce(getString(item))
        }
    }

    abstract fun doSpeakWith(entity: EntityBase)

    protected fun doNothing() {
        Debug.println("EntityMonster::doNothing()")
    }

    abstract fun doFinalCleanup()

    private fun foundBetterArmor() =
        // if we find armor in the current room...
        currentRoom.inventory.getBestArmorOrNull()?.let { bestArmor ->
            // if we already have armor equipped...
            armor?.let {
                // return whether my defense is less than best-in-room
                it.defense < bestArmor.defense
                // found armor, and I have none equipped
            } ?: true
            // didn't find armor
        } ?: false

    private fun foundBetterWeapon() =
        currentRoom.inventory.getBestWeaponOrNull()?.let { bestWeapon ->
            weapon?.let {
                it.power < bestWeapon.power
                // found a weapon, and I have nothing equipped
            } ?: true
            // didn't find a weapon
        } ?: false

    fun announce(what: String) = currentRoom.announce(what)
}