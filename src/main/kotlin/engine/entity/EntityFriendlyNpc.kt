package engine.entity

import debug.Debug
import engine.player.Player
import engine.entity.behavior.EntityAction
import engine.entity.behavior.EntityBehavior
import engine.entity.behavior.FlavorText
import engine.game.Game
import engine.game.MovementDirection
import engine.world.Connection
import engine.world.Room
import kotlin.random.Random

class EntityFriendlyNpc(
    name: String,
    level: Int,
    val job: String,
    behavior: EntityBehavior,
    arriveStringSuffix: String = "walks in"
) : EntityBase(
    name = name,
    level = level,
    keywords = listOf(name),
    attributes = EntityAttributes.defaultNpc,
    behavior = behavior,
    arriveStringSuffix = arriveStringSuffix
) {
    override val hostilesCount
        get() = currentRoom.monsters.size
    override val deadAndUnsearchedHostilesCount
        get() = currentRoom.monsters.filter { it.isDead && it.hasNotBeenSearched }.size
    override val arriveName: String
        get() = randomName
    // region strings
    private val nameWithJob = "$name the $job"
    override val randomName
        get() = when (Random.nextInt(0, 2)) {
            0 -> nameWithJob
            else -> name
        }
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
    override val deathName = nameWithJob
    override val nameForCollectionString
        get() = when {
            isDead -> "$nameWithJob (dead)"
            posture == EntityPosture.KNEELING -> "$name (kneeling)"
            posture == EntityPosture.SITTING -> "$name (sitting)"
            else -> name
        }
    // endregion

    override fun doFinalCleanup() {
        announce("The body of $nameWithJob crumbles to dust.")
        currentRoom.npcs.remove(this)
    }
    override fun doSearchRandomUnsearchedDeadHostile() {
        currentRoom.monsters.filter { it.isDead && it.hasNotBeenSearched }.randomOrNull()?.let { deadMonster ->
            announce("$name searches the ${deadMonster.name}.")

            deadMonster.weapon?.let {
                currentRoom.inventory.items.add(it)
                announce("The ${deadMonster.name} drops ${it.nameWithIndefiniteArticle}.")
            }
            deadMonster.armor?.let {
                currentRoom.inventory.items.add(it)
                announce("The ${deadMonster.name} drops ${it.nameWithIndefiniteArticle}.")
            }

            if (deadMonster.inventory.items.isNotEmpty()) {
                currentRoom.inventory.items.addAll(deadMonster.inventory.items)
                announce(deadMonster.dropString(deadMonster.inventory))
            }

            deadMonster.hasNotBeenSearched = false
        }
    }
    override fun calculateAttackPower() =
        attributes.strength + (weapon?.power ?: 0) + Debug.npcAttackBuff
    override fun doAttackRandomHostile() {
        if (posture != EntityPosture.STANDING) {
            doStand()
        } else {
            currentRoom.randomLivingMonsterOrNull()?.let { monster ->
                // npc weapon
                val weaponString = weapon?.name ?: "fists"
                // npc attack
                val attack = attributes.strength + (weapon?.power ?: 0)
                // monster defense
                val defense = monster.attributes.baseDefense
                // resultant damage
                val damage = (attack - defense).coerceAtLeast(0)

                announce("$name swings at the ${monster.name} with their $weaponString.")

                if (damage > 0) {
                    announce("They hit for $damage damage.")
                } else {
                    announce("They miss!")
                }

                monster.attributes.currentHealth -= damage
                if (monster.attributes.currentHealth <= 0) {
                    announce("The ${monster.name} dies.")

                    // TODO: npcs gain experience?
                    // experience += experience
                    // println("You've gained $experience experience.")
                }
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
    override fun doSpeakWith(entity: EntityBase) {
        if (entity == this) {
            doMumble()
        } else {
            announce("$randomName exchanges a few words with ${entity.name}.")
        }
    }
    override fun doMove(newRoom: Room, connection: Connection) {
        // Debug.println("EntityFriendlyNpc::doMove() - ${this.name} - move from ${currentRoom.coordinates} to ${newRoom.coordinates}")

        // leaving
        currentRoom.npcs.remove(this)
        announce(departString(connection))
        // move
        currentRoom = newRoom
        // arriving
        currentRoom.addNpc(this)
    }
    override fun isAlone() = currentRoom.npcs.size == 1 // self
            && currentRoom.monsters.isEmpty()
            && Player.currentRoom != currentRoom
    override fun doChatter() =
        announce(
            FlavorText.get(EntityAction.CHATTER)
                // TODO: find a way to remove this step or move somewhere else
                .replace("randomName", randomName)
        )
    override fun doAttackPlayer() {
        TODO("Not yet implemented")
    }
    override fun doFindAndEquipAnyWeapon() {
        currentRoom.inventory.getAndRemoveRandomWeaponOrNull()?.let { foundWeapon ->
            weapon?.let { oldWeapon ->
                currentRoom.inventory.items.add(oldWeapon)
                announce(dropString(oldWeapon))
            }

            weapon = foundWeapon
            say(FlavorText.get(EntityAction.GET_ANY_ITEM))
            announce(getString(foundWeapon))
        }
    }
    override fun doFindAndEquipAnyArmor() {
        currentRoom.inventory.getAndRemoveRandomArmorOrNull()?.let { foundArmor ->
            armor?.let { oldArmor ->
                currentRoom.inventory.items.add(oldArmor)
                announce(dropString(oldArmor))
            }

            armor = foundArmor
            announce(getString(foundArmor))
        }
    }
}
