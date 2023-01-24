import item.template.ItemTemplates
import world.World

object Debug {
    fun println(str: String) {
        // kotlin.io.println(str)
    }

    fun addItemsToRandomRooms() {
        repeat(30) {
            ItemTemplates.junk.random().createItemAt(World.getRandomRoom())
        }
        repeat(3) {
            ItemTemplates.food.random().createItemAt(World.getRandomRoom())
            ItemTemplates.drinks.random().createItemAt(World.getRandomRoom())
            ItemTemplates.containers.random().createItemAt(World.getRandomRoom())
            ItemTemplates.weapons.random().createItemAt(World.getRandomRoom())
            ItemTemplates.armor.random().createItemAt(World.getRandomRoom())
        }
    }
}