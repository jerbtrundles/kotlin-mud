package entity

import com.beust.klaxon.Klaxon
import game.Game

object EntityTemplates {
    var entities = listOf<EntityTemplate>()

    fun load(c: Class<() -> Unit>) {
        loadEntities(c)
    }

    private fun loadEntities(c: Class<() -> Unit>) {
        Debug.println("Loading entities...")

        val json = c.getResourceAsStream("entities.json")?.bufferedReader()?.readText()!!
        entities = Klaxon().parseArray(json)!!

        Debug.println("Done loading entities. ${entities.size} types of enemies are out to get us!")
    }
}