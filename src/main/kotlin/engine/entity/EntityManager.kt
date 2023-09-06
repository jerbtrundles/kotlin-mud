package engine.entity

import debug.Debug
import engine.utility.Common
import engine.entity.behavior.EntityBehavior
import engine.game.Game
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import engine.world.World
import kotlin.random.Random

object EntityManager {
    private var allNpcNames = listOf<String>()
    private var allNpcJobs = listOf<String>()

    fun load(c: Class<() -> Unit>) {
        allNpcNames = Common.parseArrayFromJson(c, "names.json")
        allNpcJobs = Common.parseArrayFromJson(c, "jobs.json")
    }

    private fun createRandomNpc() =
        when (Random.nextInt(100)) {
            in 0..10 -> createJanitor()
            in 11 .. 20 -> createBerserker()
            else -> createDefault()
        }


    private fun createDefault() = EntityFriendlyNpc(
        name = allNpcNames.random(),
        level = 1,
        job = allNpcJobs.random(),
        behavior = EntityBehavior.defaultNpc
    )

    private fun createJanitor() = EntityFriendlyNpc(
        name = allNpcNames.random(),
        level = 1,
        job = "janitor",
        behavior = EntityBehavior.janitor,
        arriveStringSuffix = "arrives, broom in hand"
    )

    private fun createBerserker() = EntityFriendlyNpc(
        name = allNpcNames.random(),
        level = 1,
        job = "berserker",
        behavior = EntityBehavior.berserker,
        arriveStringSuffix = "storms in"
    )

    private fun createRandomMonster() = EntityTemplates.monsterTemplates.random().create()

    suspend fun start() {
        val allMonsters = mutableListOf<EntityMonster>()
        val allNpcs = mutableListOf<EntityFriendlyNpc>()

        coroutineScope {
            while (Game.running) {
                while (allMonsters.size < Debug.maxMonsters) { // if
                    if (allMonsters.size % 5 == 0) {
                        Debug.println("Total monsters: ${allMonsters.size}")
                    }

                    val monster = createRandomMonster()
                    allMonsters.add(monster)

                    launch {
                        monster.goLiveYourLifeAndBeFree(initialRoom = World.getRandomRoom())
                    }
                }

                while (allNpcs.size < Debug.maxNpcs) {
                    if (allNpcs.size % 5 == 0) {
                        Debug.println("Total NPCs: ${allNpcs.size}")
                    }

                    val randomNpc = createRandomNpc()
                    allNpcs.add(randomNpc)

                    launch {
                        randomNpc.goLiveYourLifeAndBeFree(initialRoom = World.getRandomRoom())
                    }
                }

                Game.delay(5000)
                launch { removeSearchedMonsters(allMonsters) }
                launch { removeSearchedNpcs(allNpcs) }
                Game.delay(5000)
            }
        }
    }

    private fun removeSearchedMonsters(allMonsters: MutableList<EntityMonster>) {
        // remove searched dead
        val searched = allMonsters.filter { monster -> !monster.hasNotBeenSearched }
        allMonsters.removeAll(searched)
    }

    private fun removeSearchedNpcs(allNpcs: MutableList<EntityFriendlyNpc>) {
        // remove searched dead
        val searched = allNpcs.filter { npc -> !npc.hasNotBeenSearched }
        allNpcs.removeAll(searched)
    }
}