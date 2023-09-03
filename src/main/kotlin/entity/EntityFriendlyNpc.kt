package entity

import Inventory
import debug.Debug
import game.Game
import game.MovementDirection
import item.ItemBase
import world.Connection
import world.Room
import kotlin.random.Random

class EntityFriendlyNpc(
    name: String,
    level: Int,
    val job: String
) : EntityBase(
    name = name,
    level = level,
    keywords = listOf(name),
    attributes = EntityAttributes.defaultNpc
) {
    private val flavorTextArray = arrayOf(
        "randomName gazes up at the sky.",
        "randomName shuffles their feet.",
        "randomName glances around.",
        "randomName says \"quip\"",
        "randomName rummages around in their pockets, looking for something."
    )

    private val flavorTextQuipArray = arrayOf(
        "Nice weather today, isn't it?",
        "My feet ache somethin' awful.",
        "I'm a little sick. Don't get too close!",
        "I found a lucky coin on the ground the other day.",
        "Mrrrrrrr...."
    )

    fun flavorText(entity: EntityBase): String =
        flavorTextArray.random()
            .replace("randomName", entity.randomName)
            .replace("quip", flavorTextQuipArray.random())

    override val hostilesCount
        get() = currentRoom.monsters.size
    override val deadAndUnsearchedHostilesCount
        get() = currentRoom.monsters.filter { it.isDead && it.hasNotBeenSearched }.size


    // region strings
    private val nameWithJob = "$name the $job"

    override val randomName
        get() = when (Random.nextInt(0, 2)) {
            0 -> nameWithJob
            else -> name
        }

    override val arriveString = "$nameWithJob walks in."
    override fun departString(connection: Connection): String {
        return if (connection.direction != MovementDirection.NONE) {
            // Bob heads east.
            "$name heads ${connection.direction.toString().lowercase()}."
        } else {
            // Bob heads over to the shop.
            "$name heads over to the ${connection.matchInput.suffix}."

            // TODO: connection types that don't fit into these scenarios
            //  e.g. Bob heads through the town gates. (go gates)
        }
    }

    override val deathString = "$nameWithJob dies."
    override val sitString
        get() = "$randomName sits down."
    override val standString
        get() = "$randomName stands up."
    override val kneelString
        get() = "$randomName kneels."

    override fun putAwayString(item: ItemBase) =
        "$randomName puts away their ${item.name}."

    override fun equipString(item: ItemBase) =
        "$randomName equips ${item.nameWithIndefiniteArticle}."

    override fun removeString(item: ItemBase) =
        "$randomName removes their ${item.name}."

    override val nameForCollectionString
        get() = when {
            isDead -> "$nameWithJob (dead)"
            posture == EntityPosture.KNEELING -> "$name (kneeling)"
            posture == EntityPosture.SITTING -> "$name (sitting)"
            else -> name
        }

    override fun dropString(item: ItemBase) =
        "$randomName drops ${item.nameWithIndefiniteArticle}."
    override fun dropString(inventory: Inventory): String {
        return ""
    }

    override fun getString(item: ItemBase) =
        "$randomName picks up ${item.nameWithIndefiniteArticle}."
    // endregion

    override fun doFinalCleanup() {
        currentRoom.announce("The body of $nameWithJob crumbles to dust.")
        currentRoom.npcs.remove(this)
    }

    override fun doSearchRandomUnsearchedDeadHostile() {
        currentRoom.monsters.filter { it.isDead && it.hasNotBeenSearched }.randomOrNull()?.let { deadMonster ->
            currentRoom.announce("$name searches the ${deadMonster.name}.")

            deadMonster.weapon?.let {
                currentRoom.inventory.items.add(it)
                currentRoom.announce("The ${deadMonster.name} drops ${it.nameWithIndefiniteArticle}.")
            }
            deadMonster.armor?.let {
                currentRoom.inventory.items.add(it)
                currentRoom.announce("The ${deadMonster.name} drops ${it.nameWithIndefiniteArticle}.")
            }

            if(deadMonster.inventory.items.isNotEmpty()) {
                currentRoom.inventory.items.addAll(deadMonster.inventory.items)
                currentRoom.announce("The ${deadMonster.name} drops ${deadMonster.inventory.collectionString}.")
            }

            deadMonster.hasNotBeenSearched = false
        }
    }

    override fun calculateAttackPower() =
        attributes.strength + (weapon?.power ?: 0) + Debug.npcAttackBuff

    override fun doAttackRandomHostile() {
        currentRoom.randomLivingMonsterOrNull()?.let { monster ->
            // npc weapon
            val weaponString = weapon?.name ?: "fists"
            // npc attack
            val attack = attributes.strength + (weapon?.power ?: 0)
            // monster defense
            val defense = monster.attributes.baseDefense
            // resultant damage
            val damage = (attack - defense).coerceAtLeast(0)

            currentRoom.announce("$name swings at the ${monster.name} with their $weaponString.")

            if (damage > 0) {
                currentRoom.announce("They hit for $damage damage.")
            } else {
                currentRoom.announce("They miss!")
            }

            monster.attributes.currentHealth -= damage
            if (monster.attributes.currentHealth <= 0) {
                currentRoom.announce("The ${monster.name} dies.")

                // TODO: npcs gain experience?
                // experience += experience
                // println("You've gained $experience experience.")
            }
        }
    }

    override suspend fun doDelay() {
        // TODO: make this based off of something else
        //  e.g. entity speed, type

        // Debug.println("EntityFriendlyNpc::doDelay()")

        Game.delayRandom(
            min = Debug.npcDelayMin, max = Debug.npcDelayMax,
            conditions = listOf(
                ::hasNotBeenSearched
            )
        )
    }

    override fun doInit(initialRoom: Room) {
        currentRoom = initialRoom
        initialRoom.addNpc(this)
    }

    override fun doIdle() {
        TODO("Not yet implemented")
    }

    override fun doSpeakWith(entity: EntityBase) {
        if (entity == this) {
            currentRoom.announce("$randomName mumbles something to themselves.")
        } else {
            currentRoom.announce("$randomName exchanges a few words with ${entity.name}.")
        }
    }

    override fun doMove(newRoom: Room, connection: Connection) {
        // Debug.println("EntityFriendlyNpc::doMove() - ${this.name} - move from ${currentRoom.coordinates} to ${newRoom.coordinates}")

        // leaving
        currentRoom.npcs.remove(this)
        currentRoom.announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving
        currentRoom.addNpc(this)
    }

    // region behaviors
//    private fun normalBehavior() {
//        when (Random.nextInt(10)) {
    // 0 -> doGetRandomItemFromRoom()
//            0 -> doRandomMove()
//            1 -> doExchangeWordsWithNpc()
//            2 -> doSit()
//            3 -> doStand()
//            4, 5 -> doDropRandomItemFromInventory()
//            6 -> doFlavorText()
//            else -> doAttack()
//                6 -> doEquipWeapon()
//                7 -> doEquipArmor()
//                8 -> doRemoveWeapon()
//                9 -> doRemoveArmor()
    // else -> doDropRandomItemFromInventory()
//        }
//    }
    // endregion

    // region actions

    override fun doAttack() {
        currentRoom.monsters.filter { monster -> !monster.isDead }.randomOrNull()?.let { monster ->
            val weaponString = weapon?.name ?: "fists"
            val attack = attributes.strength + (weapon?.power ?: 0)
            val defense = monster.attributes.baseDefense
            val damage = (attack - defense).coerceAtLeast(0)

            currentRoom.announce("$name swings their $weaponString at the ${monster.name}.")

            if (damage > 0) {
                currentRoom.announce("They hit for $damage damage.")
            } else {
                currentRoom.announce("They miss!")
            }

            monster.takeDamage(damage)
        }
    }
    // endregion

    override fun isAlone() = currentRoom.npcs.size == 1 // self
            && currentRoom.monsters.isEmpty()
            && Player.currentRoom != currentRoom

    override fun doChatter() {
        currentRoom.announce(flavorText(this))
    }

    override fun doMumble() = currentRoom.announce("$name mumbles something to themselves.")

}