package entity

import com.beust.klaxon.Klaxon

object EntityTemplates {
    var monsters = listOf<EntityMonsterTemplate>()

    fun load(c: Class<() -> Unit>) {
        loadMonsters(c)
    }

    private fun loadMonsters(c: Class<() -> Unit>) {
        Debug.println("Loading monsters...")
        monsters = Common.parseArrayFromJson(c, "entities.json")
        Debug.println("Done loading monsters. ${monsters.size} types of enemies are out to get us!")
    }
}