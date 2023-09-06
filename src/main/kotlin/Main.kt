import debug.Debug
import engine.player.MyViewModel
import engine.entity.EntityManager
import engine.entity.EntityTemplates
import engine.game.Game
import engine.item.template.ItemTemplates
import kotlinx.coroutines.*
import engine.world.World
import engine.world.template.ShopTemplates

fun main() {
    init()

    runBlocking {
        launch { EntityManager.start() }
        launch { gatherInput() }
    }

    Debug.println("I'mma done!")
}

// region init
fun init() {
    loadResources()
    // add debug items, monsters, npcs
    Debug.init()
    // display initial room
    MyViewModel.onInput("look")
}

fun loadResources() {
    val c = {}.javaClass
    // load items first; no other dependencies
    ItemTemplates.load(c)
    // load npc names and jobs next; no other dependencies
    EntityManager.load(c)
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
            MyViewModel.onInput(readlnOrNull() ?: "")
        }
    }
}
