package item.template

import item.ItemBase

// TODO: add attribute variance to templates (e.g. sword damage, value, other quality attributes)

abstract class ItemTemplate(
    val name: String,
    val description: String,
    val weight: Double,
    val value: Int,
    val keywords: List<String>
) {
    abstract fun createItem(): ItemBase
}