import item.template.ItemTemplates
import world.World

object Debug {
    const val maxNpcs = 10
    const val maxMonsters = 5
    const val npcDelayMin = 500
    const val npcDelayMax = 1000
    const val monsterDelayMin = 2000
    const val monsterDelayMax = 4000
    const val monsterMaxLevel = 5

    fun println(str: String) {
        // kotlin.io.println(str)
    }

    fun addItemsToRandomRooms() {
        repeat(20) {
            ItemTemplates.weapons.random().createItemAt(World.getRandomRoom())
        }
        repeat(20) {
            ItemTemplates.armor.random().createItemAt(World.getRandomRoom())
        }
//        repeat(3) {
//            val junk = ItemTemplates.junk.random()
//            val room = World.getRandomRoom()
//            repeat(10) {
//                junk.createItemAt(room)
//            }
//        }

//        repeat(3) {
//            val food = ItemTemplates.food.random()
//            val room = World.getRandomRoom()
//            repeat(10) {
//                food.createItemAt(room)
//            }
//        }

//        repeat(30) {
//            ItemTemplates.junk.random().createItemAt(World.getRandomRoom())
//        }
//        repeat(3) {
//            ItemTemplates.food.random().createItemAt(World.getRandomRoom())
//            ItemTemplates.drinks.random().createItemAt(World.getRandomRoom())
//            ItemTemplates.containers.random().createItemAt(World.getRandomRoom())
//            ItemTemplates.weapons.random().createItemAt(World.getRandomRoom())
//            ItemTemplates.armor.random().createItemAt(World.getRandomRoom())
//        }
    }
}