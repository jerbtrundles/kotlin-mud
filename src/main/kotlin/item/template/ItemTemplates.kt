package item.template

import com.beust.klaxon.Klaxon

object ItemTemplates {
    var junk = listOf<ItemTemplateJunk>()
    var drinks = listOf<ItemTemplateDrink>()
    var food = listOf<ItemTemplateFood>()
    var containers = listOf<ItemTemplateContainer>()

    fun load(c: Class<() -> Unit>) {
        loadJunk(c)
        loadDrinks(c)
        loadFood(c)
        loadContainers(c)
    }

    private fun loadContainers(c: Class<() -> Unit>) {
        println("Loading containers...")
        val json = c.getResourceAsStream("items-container.json")?.bufferedReader()?.readText()!!
        containers = Klaxon().parseArray(json)!!
        println("Done loading containers. We can hold things in ${containers.size} types of containers.")
    }

    private fun loadFood(c: Class<() -> Unit>) {
        println("Loading food...")
        val json = c.getResourceAsStream("items-food.json")?.bufferedReader()?.readText()!!
        food = Klaxon().parseArray(json)!!
        println("Done loading food. We gots ${food.size} types of things to eat.")
    }

    private fun loadJunk(c: Class<() -> Unit>) {
        println("Loading junk...")
        val json = c.getResourceAsStream("items-junk.json")?.bufferedReader()?.readText()!!
        junk = Klaxon().parseArray(json)!!
        println("Done loading junk. Size of junk is ${junk.size}.")
    }

    private fun loadDrinks(c: Class<() -> Unit>) {
        println("Loading drinks...")
        val json = c.getResourceAsStream("items-drink.json")?.bufferedReader()?.readText()!!
        drinks = Klaxon().parseArray(json)!!
        println("Done loading drinks. We have ${drinks.size} drinks.")
    }
}