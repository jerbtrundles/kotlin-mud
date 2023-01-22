import entity.EntityBase
import entity.EntityTemplates
import game.Game
import item.template.ItemTemplates
import kotlinx.coroutines.*
import world.World

fun main() {
    init()

    runBlocking {
        launch { gatherInput() }
        launch { manageEntities() }
    }

    Game.print("I'mma done!")
}

// region init
fun init() {
    loadResources()
    debugAddItemsToRandomRooms()
    MyViewModel.onInput("look")
}

fun debugAddItemsToRandomRooms() {
    repeat(3) {
        ItemTemplates.junk.random().createItemAt(World.getRandomRoom())
        ItemTemplates.food.random().createItemAt(World.getRandomRoom())
        ItemTemplates.drinks.random().createItemAt(World.getRandomRoom())
        ItemTemplates.containers.random().createItemAt(World.getRandomRoom())
        ItemTemplates.weapons.random().createItemAt(World.getRandomRoom())
        ItemTemplates.armor.random().createItemAt(World.getRandomRoom())
    }
}
fun loadResources() {
    val c = {}.javaClass
    World.load(c)
    ItemTemplates.load(c)
    EntityTemplates.load(c)
}
// endregion

suspend fun gatherInput() {
    withContext(Dispatchers.IO) {
        while (Game.running) {
            MyViewModel.onInput(readLine() ?: "")
        }
    }
}
suspend fun manageEntities() {
    val maxEntities = 1
    val allEntities = mutableListOf<EntityBase>()

    coroutineScope {
        while (Game.running) {
            delay(500)
            removeSearchedEntities(allEntities)
            delay(500)
            if (allEntities.size < maxEntities) {
//                if (allEntities.size % 5 == 0) {
//                    Game.print("Total entities: ${allEntities.size}")
//                }

                val entity = EntityTemplates.entities.random().create()
                allEntities.add(entity)

                launch {
                    entity.goLiveYourLifeAndBeFree(initialRoom = World.zero) // World.getRandomRoom()) }
                }
            }
        }
    }
}

fun removeSearchedEntities(allEntities: MutableList<EntityBase>) {
    // remove searched dead
    val searched = allEntities.filter { entity -> entity.hasBeenSearched }
    if (searched.isNotEmpty()) {
        // remove searched from room inventories
        searched.forEach { entity ->
            entity.currentRoom.entities.remove(entity)
        }
        allEntities.removeAll(searched)
    }
}

