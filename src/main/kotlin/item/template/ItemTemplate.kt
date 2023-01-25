package item.template

import item.ItemBase
import world.Room

// TODO: add attribute variance to templates (e.g. sword damage, value, other quality attributes)

abstract class ItemTemplate(
    val name: String,
    val description: String,
    val weight: Double,
    val value: Int,
    val keywords: List<String>
) {
    abstract fun createItem(): ItemBase
    fun createItemAt(room: Room) = room.inventory.items.add(createItem())
    fun matches(str: String): Boolean {
        return name == str
                || keywords.contains(str)
    }
}