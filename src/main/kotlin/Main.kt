import item.template.ItemTemplates
import kotlinx.coroutines.*
import world.World

fun main() {
  val c = {}.javaClass
  World.load(c)
  ItemTemplates.load(c)

  repeat(10) {
    World.getRandomRoom().inventory.items.add(ItemTemplates.junk.random().createItem())
    World.getRandomRoom().inventory.items.add(ItemTemplates.drinks.random().createItem())
    World.getRandomRoom().inventory.items.add(ItemTemplates.food.random().createItem())
    World.getRandomRoom().inventory.items.add(ItemTemplates.containers.random().createItem())
    World.getRandomRoom().inventory.items.add(ItemTemplates.weapons.random().createItem())
    World.getRandomRoom().inventory.items.add(ItemTemplates.armor.random().createItem())
  }

  MyViewModel.onInput("look")

  runBlocking {
    launch { gatherInput() }
    launch { allowForOutput() }
  }

  println("I'mma done!")
}

suspend fun gatherInput() {
  withContext(Dispatchers.IO) {
    do {
      val line = readLine() ?: ""
      MyViewModel.onInput(line)
    } while (line != "quit")
  }
}

suspend fun allowForOutput() {
  withContext(Dispatchers.IO) {
//        for (i in 0 .. 10) {
//            delay(2000L)
//            println("Hello from allowForOutput!")
//        }
  }
}
