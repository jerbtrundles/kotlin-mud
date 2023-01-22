package item.template

import com.beust.klaxon.Klaxon
import game.Game

object ItemTemplates {
    var junk = listOf<ItemTemplateJunk>()
    var drinks = listOf<ItemTemplateDrink>()
    var food = listOf<ItemTemplateFood>()
    var containers = listOf<ItemTemplateContainer>()
    var weapons = listOf<ItemTemplateWeapon>()
    var armor = listOf<ItemTemplateArmor>()

    fun load(c: Class<() -> Unit>) {
        loadJunk(c)
        loadDrinks(c)
        loadFood(c)
        loadContainers(c)
        loadWeapons(c)
        loadArmor(c)
    }

    private fun loadArmor(c: Class<() -> Unit>) {
        Game.print("Loading armor...")
        val json = c.getResourceAsStream("items-armor.json")?.bufferedReader()?.readText()!!
        armor = Klaxon().parseArray(json)!!
        Game.print("Done loading armor. We can defend ourselves with ${armor.size} different options.")
    }

    private fun loadWeapons(c: Class<() -> Unit>) {
        Game.print("Loading weapons...")
        val json = c.getResourceAsStream("items-weapon.json")?.bufferedReader()?.readText()!!
        weapons = Klaxon().parseArray(json)!!
        Game.print("Done loading weapons. We can kill enemies in ${weapons.size} different ways.")
    }

    private fun loadContainers(c: Class<() -> Unit>) {
        Game.print("Loading containers...")
        val json = c.getResourceAsStream("items-container.json")?.bufferedReader()?.readText()!!
        containers = Klaxon().parseArray(json)!!
        Game.print("Done loading containers. We can hold things in ${containers.size} types of containers.")
    }

    private fun loadFood(c: Class<() -> Unit>) {
        Game.print("Loading food...")
        val json = c.getResourceAsStream("items-food.json")?.bufferedReader()?.readText()!!
        food = Klaxon().parseArray(json)!!
        Game.print("Done loading food. We gots ${food.size} types of things to eat.")
    }

    private fun loadJunk(c: Class<() -> Unit>) {
        Game.print("Loading junk...")
        val json = c.getResourceAsStream("items-junk.json")?.bufferedReader()?.readText()!!
        junk = Klaxon().parseArray(json)!!
        Game.print("Done loading junk. Size of junk is ${junk.size}.")
    }

    private fun loadDrinks(c: Class<() -> Unit>) {
        Game.print("Loading drinks...")
        val json = c.getResourceAsStream("items-drink.json")?.bufferedReader()?.readText()!!
        drinks = Klaxon().parseArray(json)!!
        Game.print("Done loading drinks. We have ${drinks.size} drinks.")
    }
}