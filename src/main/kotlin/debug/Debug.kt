package debug

import item.template.ItemTemplates
import world.World

object Debug {
    private const val debugging = true

    const val maxNpcs = 1
    const val maxMonsters = 1
    const val npcDelayMin = 500
    const val npcDelayMax = 1000
    const val monsterDelayMin = 2000
    const val monsterDelayMax = 4000
    const val monsterMaxLevel = 5
    private const val initialWeapons = 20
    private const val initialArmor = 20
    private const val initialJunk = 5
    private const val initialFood = 20
    private const val initialDrink = 20
    private const val initialContainer = 3

    fun println(str: String) {
        if(debugging) {
            kotlin.io.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t[$str]")
        }
    }

    fun init() {
        addItemsToRandomRooms()
    }

    private fun addItemsToRandomRooms() {
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
}