import item.template.ItemTemplates
import world.World

object Debug {
    fun println(str: String) {
        // kotlin.io.println(str)
    }

    fun addItemsToRandomRooms() {

        repeat(3) {
            val junk = ItemTemplates.junk.random()
            val room = World.getRandomRoom()
            repeat(10) {
                junk.createItemAt(room)
            }
        }

        repeat(3) {
            val food = ItemTemplates.food.random()
            val room = World.getRandomRoom()
            repeat(10) {
                food.createItemAt(room)
            }
        }

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