package engine.entity

import debug.Debug
import engine.player.Player
import engine.entity.behavior.EntityBehavior
import engine.game.Game
import engine.game.MovementDirection
import engine.item.ItemArmor
import engine.item.ItemWeapon
import engine.utility.withIndefiniteArticle
import engine.world.Connection
import engine.world.Room

class EntityMonster(
    monsterName: String,
    level: Int,
    keywords: List<String>,
    attributes: EntityAttributes,
    val experience: Int,
    val gold: Int,
    behavior: EntityBehavior = EntityBehavior.defaultMonster,
    weapon: ItemWeapon? = null,
    armor: ItemArmor? = null,
    arriveStringSuffix: String = "has arrived",
    stringPrefix: String = "The "
) : EntityBase(
    name = monsterName,
    level = level,
    keywords = keywords,
    attributes = attributes,
    behavior = behavior,
    weapon = weapon,
    armor = armor,
    stringPrefix = stringPrefix,
    arriveStringSuffix = arriveStringSuffix
) {
    override val randomName = name
    override val hostilesCount
        get() = currentRoom.npcs.size
    override val deadAndUnsearchedHostilesCount
        get() = currentRoom.npcs.filter { it.isDead && it.hasNotBeenSearched }.size
    override val nameForStory = "The $name"
    override val deathName = name
    override val nameForCollectionString
        get() = when {
            isDead && !hasNotBeenSearched -> "dead $name (searched)"
            isDead -> "dead $name"
            posture == EntityPosture.KNEELING -> "$name (kneeling)"
            posture == EntityPosture.SITTING -> "$name (sitting)"
            else -> name
        }
    override val arriveName = name.withIndefiniteArticle(capitalized = true)
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
    override fun doFinalCleanup() {
        announce("The body of the $name crumbles to dust.")
        currentRoom.monsters.remove(this)
    }
    override fun calculateAttackPower() =
        attributes.strength + (weapon?.power ?: 0) - Debug.monsterAttackDebuff // debug monster attack debuff
    override fun doSearchRandomUnsearchedDeadHostile() {
        currentRoom.npcs.filter { it.isDead && it.hasNotBeenSearched }.randomOrNull()?.let { deadNpc ->
            announce("The $name searches the corpse of ${deadNpc.name}.")

            deadNpc.weapon?.let {
                currentRoom.inventory.items.add(it)
                announce("${deadNpc.name} drops ${it.nameWithIndefiniteArticle}.")
            }
            deadNpc.armor?.let {
                currentRoom.inventory.items.add(it)
                announce("${deadNpc.name} drops ${it.nameWithIndefiniteArticle}.")
            }

            if (deadNpc.inventory.items.isNotEmpty()) {
                currentRoom.inventory.items.addAll(deadNpc.inventory.items)
                announce(deadNpc.dropString(deadNpc.inventory))
            }

            deadNpc.hasNotBeenSearched = false
        }
    }
    override fun doAttackRandomHostile() {
        currentRoom.randomLivingNpcOrNull()?.let { npc ->
            // monster weapon
            val weaponString = weapon?.name ?: "fists"
            // monster attack
            val attack = calculateAttackPower()
            // npc defense
            val defense = npc.attributes.baseDefense
            // resultant damage
            val damage = (attack - defense).coerceAtLeast(0)

            announce("The $name swings at the ${npc.name} with their $weaponString.")

            if (damage > 0) {
                announce("They hit for $damage damage.")
            } else {
                announce("They miss!")
            }

            npc.attributes.currentHealth -= damage
            if (npc.attributes.currentHealth <= 0) {
                announce("${npc.name} dies.")

                // TODO: monsters gain experience?
                // experience += experience
                // announce("You've gained $experience experience.")
            }
        }
    }
    override fun isAlone() = currentRoom.monsters.size == 1 // self
            && currentRoom.npcs.isEmpty()
            && Player.currentRoom != currentRoom
    override fun doInit(initialRoom: Room) {
        // set initial room and add self
        currentRoom = initialRoom
        currentRoom.addMonster(this)

        Debug.println("EntityMonster::doInit() - adding ${this.name} to ${currentRoom.coordinates}")
    }
    override suspend fun doDelay() {
        // TODO: make this based off of something else
        //  e.g. entity speed, type

        // Debug.println("EntityMonster::doDelay()")
        Game.delayRandom(
            min = Debug.monsterDelayMin, max = Debug.monsterDelayMax,
            conditions = listOf(
                ::hasNotBeenSearched
            )
        )
    }
    override fun doSpeakWith(entity: EntityBase) {
        if (entity == this) {
            doMumble()
        } else {
            // TODO: separate npc from monster
            announce("The $name exchanges a few words with ${entity.name}.")
        }
    }
    override fun doMove(newRoom: Room, connection: Connection) {
        // debug.Debug.println("EntityMonster::doRandomMove() - ${this.name} - move from ${currentRoom.coordinates} to ${newRoom.coordinates}")

        // leaving
        currentRoom.monsters.remove(this)
        announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving (addMonster handles announce)
        currentRoom.addMonster(this)
    }
    override fun doChatter() = chatterFunctions.random()()

    private val chatterFunctions = arrayOf(
        ::doMumble
    )
    override fun doAttackPlayer() {
        if (posture != EntityPosture.STANDING) {
            doStand()
        } else if (Player.isAlive) {
            // already verified if evaluated as a situation
            // TODO: can this method be called any other way?
            //  if so, might need to add verification

            // npc weapon
            val weaponString = weapon?.name ?: "fists"
            // npc attack
            val attack = attributes.strength + (weapon?.power ?: 0)
            // player defense
            val defense = Player.attributes.baseDefense
            // resultant damage
            val damage = (attack - defense).coerceAtLeast(0)

            announce("The $name swings at you with their $weaponString.")

            if (damage > 0) {
                announce("They hit for $damage damage.")
            } else {
                announce("They miss!")
            }

            Player.attributes.currentHealth -= damage
            if (Player.attributes.currentHealth <= 0) {
                announce("You die!")

                // TODO: npcs gain experience?
                // experience += experience
                // println("You've gained $experience experience.")
            }
        }
    }
    override fun doFindAndEquipAnyWeapon() {
        currentRoom.inventory.getAndRemoveRandomWeaponOrNull()?.let { foundWeapon ->
            weapon?.let { oldWeapon ->
                announce(dropString(oldWeapon))
                currentRoom.inventory.items.add(oldWeapon)
            }

            weapon = foundWeapon
            announce(getString(foundWeapon))
        }
    }
    override fun doFindAndEquipAnyArmor() {
        currentRoom.inventory.getAndRemoveRandomArmorOrNull()?.let { foundArmor ->
            armor?.let { oldArmor ->
                announce(dropString(oldArmor))
                currentRoom.inventory.items.add(oldArmor)
            }

            armor = foundArmor
            announce(getString(foundArmor))
        }
    }
}