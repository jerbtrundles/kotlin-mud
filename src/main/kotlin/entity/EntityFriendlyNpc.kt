package entity

import game.Game
import item.ItemArmor
import item.ItemWeapon
import world.Connection
import world.Room
import world.World
import kotlin.random.Random

class EntityFriendlyNpc(
    name: String,
    val job: String
) : EntityBase(
    name = name,
    keywords = listOf(name),
    EntityAttributes.defaultNpc
) {
    val nameWithJob = "$name the $job"
    val randomName
        get() = if (Random.nextInt(0, 2) == 0) {
            nameWithJob
        } else {
            name
        }

    override val arriveString = "$nameWithJob walks in."
    override fun departString(connection: Connection): String {
        return "$nameWithJob heads ${connection.direction.toString().lowercase()}."
    }

    override val nameForCollectionString
        get() = nameWithJob

    companion object {
        val flavorTextArray = arrayOf(
            "randomName gazes up at the sky.",
            "randomName shuffles their feet.",
            "randomName glances around.",
            "randomName says \"quip\"",
            "randomName rummages around in their pockets, looking for something."
        )

        val quipArray = arrayOf(
            "Nice weather today, isn't it?",
            "My feet ache somethin' awful.",
            "I'm a little sick. Don't get too close!",
            "I found a lucky coin on the ground the other day.",
            "Mrrrrrrr...."
        )

        fun flavorText(npc: EntityFriendlyNpc) =
            flavorTextArray.random()
                .replace("randomName", npc.randomName)
                .replace("quip", quipArray.random())
    }

    override suspend fun goLiveYourLifeAndBeFree(initialRoom: Room) {
        currentRoom = initialRoom
        initialRoom.addNpc(this)

        while (Game.running) {
            // TODO: make this based off of something else
            //  e.g. entity speed, type
            Common.delayRandom(from = Debug.npcDelayMin, to = Debug.npcDelayMax)
            doAction()
        }
    }

    override fun doAction() {
        if (weapon == null) {
            when (Random.nextInt(3)) {
                0 -> doEquipWeapon()
                1 -> doRandomMove()
                2 -> doGetRandomItemFromRoom()
            }
        } else if (armor == null) {
            when (Random.nextInt(4)) {
                0 -> doEquipArmor()
                1 -> doRandomMove()
                2 -> doGetRandomItemFromRoom()
                3 -> doAttack()
            }
        } else {
            when (Random.nextInt(10)) {
                // 0 -> doGetRandomItemFromRoom()
                0 -> doRandomMove()
                1 -> doExchangeWordsWithNpc()
                2 -> doSit()
                3 -> doStand()
                4, 5 -> doDropRandomItemFromInventory()
                6 -> doFlavorText()
                else -> doAttack()
//                6 -> doEquipWeapon()
//                7 -> doEquipArmor()
//                8 -> doRemoveWeapon()
//                9 -> doRemoveArmor()
                // else -> doDropRandomItemFromInventory()
            }
        }
    }

    private fun doFlavorText() {
        currentRoom.announce(flavorText(this))
    }

    private fun doAttack() {
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

            monster.attributes.currentHealth -= damage
            if (monster.attributes.currentHealth <= 0) {
                currentRoom.announce("The ${monster.name} dies.")
            }
        }
    }

    private fun doEquipWeapon() {
        if (weapon != null) {
            return
        }

        inventory.getRandomTypedItem<ItemWeapon>()?.let { weaponToEquip ->
            weapon = weaponToEquip
            currentRoom.announce("$randomName equips ${weaponToEquip.nameWithIndefiniteArticle}.")
        }
    }

    private fun doEquipArmor() {
        if (armor != null) {
            return
        }

        inventory.getRandomTypedItem<ItemArmor>()?.let { armorToEquip ->
            armor = armorToEquip
            currentRoom.announce("$randomName equips ${armorToEquip.nameWithIndefiniteArticle}.")
        }
    }

    private fun doRemoveArmor() {
        armor?.let {
            currentRoom.announce("$randomName removes their ${it.name}.")
            inventory.items.add(it)
            armor = null
        }
    }

    private fun doRemoveWeapon() {
        weapon?.let {
            currentRoom.announce("$randomName puts away their ${it.name}.")
            inventory.items.add(it)
            weapon = null
        }
    }

    private fun doSit() {
        if (posture != EntityPosture.SITTING) {
            posture = EntityPosture.SITTING
            currentRoom.announce("$randomName sits down.")
        }
    }

    private fun doStand() {
        if (posture != EntityPosture.STANDING) {
            posture = EntityPosture.STANDING
            currentRoom.announce("$randomName stands up.")
        }
    }

    private fun doExchangeWordsWithNpc() {
        val npc = currentRoom.npcs.random()
        if (npc == this) {
            currentRoom.announce("$randomName mumbles something to themselves.")
        } else {
            currentRoom.announce("$randomName exchanges a few words with ${npc.randomName}.")
        }
    }

    private fun doGetRandomItemFromRoom() {
        currentRoom.inventory.items.randomOrNull()?.let { item ->
            inventory.items.add(item)
            currentRoom.inventory.items.remove(item)

            currentRoom.announce("$randomName picks up ${item.nameWithIndefiniteArticle}.")
        }
    }

    private fun doDropRandomItemFromInventory() {
        inventory.items.randomOrNull()?.let { item ->
            currentRoom.inventory.items.add(item)
            inventory.items.remove(item)

            currentRoom.announce("$randomName drops ${item.nameWithIndefiniteArticle}.")
        }
    }

    override fun doRandomMove() {
        if (posture == EntityPosture.STANDING) {
            val connection = currentRoom.connections.random()
            val newRoom = World.getRoomFromCoordinates(connection.coordinates)

            // leaving
            currentRoom.npcs.remove(this)
            currentRoom.announce(departString(connection))
            // move
            currentRoom = newRoom
            // arriving
            currentRoom.addNpc(this)

            // if directional
            // otherwise (The goblin goes through the gates.)
        }
    }
}