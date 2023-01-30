import item.template.ItemTemplates
import world.World

object Debug {
    const val maxNpcs = 1
    const val maxMonsters = 1
    const val npcDelayMin = 500
    const val npcDelayMax = 1000
    const val monsterDelayMin = 2000
    const val monsterDelayMax = 4000
    const val monsterMaxLevel = 5
    const val initialWeapons = 20
    const val initialArmor = 20
    const val initialJunk = 5
    const val initialFood = 20
    const val initialDrink = 20
    const val initialContainer = 3

    fun println(str: String) {
        // kotlin.io.println(str)
    }

    fun addItemsToRandomRooms() {
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