package item

import com.beust.klaxon.Json
import item.template.ItemTemplateJunk

class ItemJunk(
    name: String,
    description: String,
    weight: Double,
    value: Int,
    keywords: List<String>,
    @Json(ignored = true)
    val junkFactor: Int = 5
) : ItemBase(name, description, weight, value, keywords) {
    constructor(template: ItemTemplateJunk): this(
        template.name,
        template.description,
        template.weight,
        template.value,
        template.keywords
    )
}