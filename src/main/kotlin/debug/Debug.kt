package debug

import engine.entity.EntityBase
import engine.entity.behavior.EntitySituation
import engine.game.Game
import engine.item.template.ItemTemplates
import engine.world.World

object Debug {
    private class Level
    private const val debugging = false

    const val valuableItemMinimumValue = 200
    const val maxNpcs = 10
    const val maxMonsters = 6
    const val npcDelayMin = 2000
    const val npcDelayMax = 3000
    const val monsterDelayMin = 3000
    const val monsterDelayMax = 4000
    const val monsterMaxLevel = 5
    const val monsterAttackDebuff = 0
    const val npcAttackBuff = 0
    private const val initialWeapons = 20
    private const val initialArmor = 20
    private const val initialJunk = 0
    private const val initialExpensiveJunk = 10
    private const val initialFood = 0
    private const val initialDrink = 0
    private const val initialContainer = 0

    fun println(str: String) {
        if(debugging) {
            kotlin.io.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t[$str]")
        }
    }

    fun init() {
        addRandomItemsToRandomRooms()
        addExpensiveJunk()
    }

    private fun addExpensiveJunk() {
        repeat(initialExpensiveJunk) {
            ItemTemplates.junk[0].createItemAt(World.getRandomRoom())
        }
    }
    private fun addRandomItemsToRandomRooms() {
        repeat(initialWeapons) {
            ItemTemplates.weapons.random().createItemAt(World.getRandomRoom())
        }
        repeat(initialArmor) {
            ItemTemplates.armor.random().createItemAt(World.getRandomRoom())
        }
        repeat(initialJunk) {
            ItemTemplates.junk.random().createItemAt(World.getRandomRoom())
        }
        repeat(initialFood) {
            ItemTemplates.food.random().createItemAt(World.getRandomRoom())
        }
        repeat(initialDrink) {
            ItemTemplates.drinks.random().createItemAt(World.getRandomRoom())
        }
        repeat(initialContainer) {
            ItemTemplates.containers.random().createItemAt(World.getRandomRoom())
        }
    }

    fun assessSituations(entity: EntityBase) {
        EntitySituation.values().forEach { situation ->
            Game.println("$situation: ${entity.isInSituation(situation)}")
        }
    }
}