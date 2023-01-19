package item.template

import item.ItemWeapon

class ItemTemplateWeapon(
    name: String,
    description: String,
    weight: Double,
    value: Int,
    keywords: List<String>,
    val power: Int
): ItemTemplate(name, description, weight, value, keywords) {
    override fun createItem() = ItemWeapon(this)
}