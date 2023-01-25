package world.template

import com.beust.klaxon.Klaxon

object ShopTemplates {
    var templates = listOf<ShopTemplate>()

    fun load(c: Class<() -> Unit>) {
        try {
            val json = c.getResourceAsStream("shops.json")?.bufferedReader()?.readText()!!
            templates = Klaxon().parseArray(json)!!
        } catch (e: Exception) {
            println(e.message)
        }
    }
}