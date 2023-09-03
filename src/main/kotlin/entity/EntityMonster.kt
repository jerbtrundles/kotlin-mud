package entity

import Inventory
import debug.Debug
import game.Game
import game.MovementDirection
import item.ItemBase
import withIndefiniteArticle
import world.Connection
import world.Room

class EntityMonster(
    name: String,
    level: Int,
    keywords: List<String>,
    attributes: EntityAttributes,
    val experience: Int,
    val gold: Int
) : EntityBase(name, level, keywords, attributes) {
    override val randomName = name
    override val hostilesCount
        get() = currentRoom.npcs.size + if (Player.currentRoom == currentRoom) {
            1
        } else {
            0
        }
    override val deadAndUnsearchedHostilesCount
        get() = currentRoom.npcs.filter { it.isDead && it.hasNotBeenSearched }.size

    override val nameForStory = "The $name"
    override val nameForCollectionString
        get() = when {
            isDead && !hasNotBeenSearched -> "dead $name (searched)"
            isDead -> "dead $name"
            posture == EntityPosture.KNEELING -> "$name (kneeling)"
            posture == EntityPosture.SITTING -> "$name (sitting)"
            else -> name
        }

    override val sitString
        get() = "The $name sits down."
    override val standString
        get() = "The $name stands up."
    override val kneelString
        get() = "The $name kneels."

    override fun putAwayString(item: ItemBase) =
        "The $name puts away their ${item.name}."

    override fun equipString(item: ItemBase) =
        "The $name equips ${item.nameWithIndefiniteArticle}."

    override fun removeString(item: ItemBase) =
        "The $name removes their ${item.name}."

    override fun dropString(item: ItemBase) =
        "The $name drops ${item.nameWithIndefiniteArticle}."
    override fun dropString(inventory: Inventory) =
        "The $name drops ${inventory.collectionString}."
    override fun getString(item: ItemBase) =
        "The $name picks up ${item.nameWithIndefiniteArticle}."

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

    override fun doFinalCleanup() {
        currentRoom.announce("The body of the $name crumbles to dust.")
        currentRoom.monsters.remove(this)
    }

    override fun doAttack() {
        TODO("Not yet implemented")
    }

    override fun calculateAttackPower() =
        attributes.strength + (weapon?.power ?: 0) - Debug.monsterAttackDebuff // debug monster attack debuff

    override fun doSearchRandomUnsearchedDeadHostile() {
        currentRoom.npcs.filter { it.isDead && it.hasNotBeenSearched }.randomOrNull()?.let { deadNpc ->
            currentRoom.announce("The $name searches the corpse of ${deadNpc.name}.")

            deadNpc.weapon?.let {
                currentRoom.inventory.items.add(it)
                currentRoom.announce("${deadNpc.name} drops ${it.nameWithIndefiniteArticle}.")
            }
            deadNpc.armor?.let {
                currentRoom.inventory.items.add(it)
                currentRoom.announce("${deadNpc.name} drops ${it.nameWithIndefiniteArticle}.")
            }

            if(deadNpc.inventory.items.isNotEmpty()) {
                currentRoom.inventory.items.addAll(deadNpc.inventory.items)
                currentRoom.announce("${deadNpc.name} drops ${deadNpc.inventory.collectionString}.")
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

            currentRoom.announce("The $name swings at the ${npc.name} with their $weaponString.")

            if (damage > 0) {
                currentRoom.announce("They hit for $damage damage.")
            } else {
                currentRoom.announce("They miss!")
            }

            npc.attributes.currentHealth -= damage
            if (npc.attributes.currentHealth <= 0) {
                currentRoom.announce("${npc.name} dies.")

                // TODO: monsters gain experience?
                // experience += experience
                // currentRoom.announce("You've gained $experience experience.")
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

    override fun doIdle() {
        // TODO: other idle actions
        Debug.println("EntityMonster::doIdle()")
        doNothing()
    }

    override fun doSpeakWith(entity: EntityBase) {
        if (entity == this) {
            doMumble()
        } else {
            // TODO: separate npc from monster
            currentRoom.announce("The $name exchanges a few words with ${entity.name}.")
        }
    }


    override fun doMove(newRoom: Room, connection: Connection) {
        // debug.Debug.println("EntityMonster::doRandomMove() - ${this.name} - move from ${currentRoom.coordinates} to ${newRoom.coordinates}")

        // leaving
        currentRoom.monsters.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving (addMonster handles announce)
        currentRoom.addMonster(this)
    }

    override fun doChatter() {
        doMumble()
    }

    override fun doMumble() = currentRoom.announce("The $name mumbles something to themselves.")
}