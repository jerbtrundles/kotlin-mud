package entity

import game.Game
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import world.World

object EntityManager {
    suspend fun start() {
        val maxEntities = 20
        val allEntities = mutableListOf<EntityBase>()

        coroutineScope {
            while (Game.running) {
                delay(500)
                removeSearchedEntities(allEntities)
                delay(500)
                if (allEntities.size < maxEntities) {
                    if (allEntities.size % 5 == 0) {
                        Debug.println("Total entities: ${allEntities.size}")
                    }

                    val entity = EntityTemplates.entities.random().create()
                    allEntities.add(entity)

                    launch {
                        entity.goLiveYourLifeAndBeFree(initialRoom = World.zero) // World.getRandomRoom()) }
                    }
                }
            }
        }
    }

    private fun removeSearchedEntities(allEntities: MutableList<EntityBase>) {
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
}