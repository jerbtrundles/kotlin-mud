package world.template

import com.beust.klaxon.Klaxon

object ShopTemplates {
    var templates = listOf<ShopTemplate>()

    fun load(c: Class<() -> Unit>) {
        try {
            templates = Common.parseArrayFromJson(c, "shops.json")
        } catch (e: Exception) {
            println(e.message)
        }
    }
}