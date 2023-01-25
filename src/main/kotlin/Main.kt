import entity.EntityManager
import entity.EntityTemplates
import game.Game
import item.template.ItemTemplates
import kotlinx.coroutines.*
import world.World
import world.template.ShopTemplates

fun main() {
    init()

    runBlocking {
        launch { gatherInput() }
        launch { EntityManager.start() }
    }

    Game.println("I'mma done!")
}

// region init
fun init() {
    loadResources()
    Debug.addItemsToRandomRooms()
    MyViewModel.onInput("look")
}

fun loadResources() {
    val c = {}.javaClass
    // load items first; no other dependencies
    ItemTemplates.load(c)
    // load entities next; depends on items
    EntityTemplates.load(c)
    // load shops next; depends on items
    ShopTemplates.load(c)
    // load world next; depends on shops
    World.load(c)
}
// endregion

suspend fun gatherInput() {
    withContext(Dispatchers.IO) {
        while (Game.running) {
            MyViewModel.onInput(readLine() ?: "")
        }
    }
}
