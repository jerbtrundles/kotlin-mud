package entity

import com.beust.klaxon.Json

class EntityAttributes(
    var strength: Int = 20,
    var intelligence: Int = 20,
    var vitality: Int = 20,
    var speed: Int = 20,
    @Json(name = "defense")
    var baseDefense: Int = 20,
    @Json(name = "health")
    var maximumHealth: Int = 20,
    @Json(ignored = true)
    var currentHealth: Int = maximumHealth,
    @Json(name = "magic")
    var maximumMagic: Int = 20,
    @Json(ignored = true)
    var currentMagic: Int = maximumMagic,
) {
    val healthString
        get() = "Health: $currentHealth/$maximumHealth"
    val magicString
        get() = "Magic: $currentMagic/$maximumMagic"

    companion object {
        val defaultNpc
            get() = EntityAttributes(
                strength = 3,
                intelligence = 3,
                vitality = 3,
                speed = 3,
                baseDefense = 3,
                maximumHealth = 3,
                maximumMagic = 3
            )
    }
}