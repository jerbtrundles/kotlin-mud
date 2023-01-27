package entity

import game.Game
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import world.World

object EntityManager {
    var allNpcNames = listOf<String>()
    var allNpcJobs = listOf<String>()

    fun load(c: Class<() -> Unit>) {
        allNpcNames = Common.parseArrayFromJson(c, "names.json")
        allNpcJobs = Common.parseArrayFromJson(c, "jobs.json")
    }

    fun createNpc(): EntityFriendlyNpc =
        EntityFriendlyNpc(
            name = allNpcNames.random(),
            job = allNpcJobs.random()
        )

    suspend fun start() {
        val allMonsters = mutableListOf<EntityMonster>()
        val allNpcs = mutableListOf<EntityFriendlyNpc>()

        coroutineScope {
            while (Game.running) {
                Game.delay(5000)
                removeSearchedMonsters(allMonsters)
                Game.delay(5000)

                if (allMonsters.size < Debug.maxMonsters) {
                    if (allMonsters.size % 5 == 0) {
                        Debug.println("Total monsters: ${allMonsters.size}")
                    }

                    val monster = EntityTemplates.monsterTemplates.random().create()
                    allMonsters.add(monster)

                    launch {
                        monster.goLiveYourLifeAndBeFree(initialRoom = World.getRandomRoom())
                    }
                }

                if (allNpcs.size < Debug.maxNpcs) {
                    if (allNpcs.size % 5 == 0) {
                        Debug.println("Total NPCs: ${allNpcs.size}")
                    }

                    val npc = createNpc()
                    allNpcs.add(npc)

                    launch {
                        npc.goLiveYourLifeAndBeFree(initialRoom = World.getRandomRoom())
                    }
                }
            }
        }
    }

    private fun removeSearchedMonsters(allMonsters: MutableList<EntityMonster>) {
        // remove searched dead
        val searched = allMonsters.filter { monster -> monster.hasBeenSearched }
        if (searched.isNotEmpty()) {
            // remove searched from room inventories
            searched.forEach { monster ->
                monster.currentRoom.monsters.remove(monster)
            }
            allMonsters.removeAll(searched)
        }
    }
}