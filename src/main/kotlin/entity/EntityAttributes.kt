package entity

class EntityAttributes(
    var strength: Int = 20,
    var intelligence: Int = 20,
    var vitality: Int = 20,
    var currentHealth: Int = 20,
    var maximumHealth: Int = 20,
    var currentMagic: Int = 20,
    var maximumMagic: Int = 20
) {
    val healthString
        get() = "Health: $currentHealth/$maximumHealth"
    val magicString
        get() = "Magic: $currentMagic/$maximumMagic"
}