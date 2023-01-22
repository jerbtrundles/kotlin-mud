package entity

import com.beust.klaxon.Json

class EntityTemplate(
    val name: String,
    // level and attributes remain constant
    val level: Int,
    val attributes: EntityAttributes,
    val keywords: List<String>,
    val experience: Int,
    val gold: Int,
    @Json(ignored = true)
    val inventory: EntityInventory = EntityInventory()
) {
    fun create() = EntityBase(
        level = this.level,
        name = this.name,
        experience = this.experience,
        gold = this.gold,
        attributes = EntityAttributes(
            strength = this.attributes.strength,
            intelligence = this.attributes.intelligence,
            vitality = this.attributes.vitality,
            speed = this.attributes.speed,
            baseDefense = this.attributes.baseDefense,
            maximumHealth = this.attributes.maximumHealth,
            maximumMagic = this.attributes.maximumMagic
        ),
        inventory = this.inventory,
        keywords = this.keywords
    )
}