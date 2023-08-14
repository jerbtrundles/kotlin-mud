package entity

import debug.Debug

object EntityTemplates {
    var  monsterTemplates = listOf<EntityMonsterTemplate>()

    fun load(c: Class<() -> Unit>) {
        loadMonsterTemplates(c)
    }

    private fun loadMonsterTemplates(c: Class<() -> Unit>) {
        Debug.println("Loading monsters...")
        monsterTemplates =
            Common.parseArrayFromJson<EntityMonsterTemplate>(c, "entities.json").filter { monsterTemplate ->
                monsterTemplate.level < Debug.monsterMaxLevel
            }
        Debug.println("Done loading monsters. ${monsterTemplates.size} types of enemies are out to get us!")
    }
}