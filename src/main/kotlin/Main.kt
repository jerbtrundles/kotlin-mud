import entity.EntityManager
import entity.EntityTemplates
import game.Game
import item.template.ItemTemplates
import kotlinx.coroutines.*
import world.World

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
