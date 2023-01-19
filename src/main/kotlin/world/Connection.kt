package world

import com.beust.klaxon.Json
import game.GameInput

open class Connection(
    @Json(name = "coordinates")
    val coordinatesString: String,
    @Json(name = "input")
    val matchInputString: String,
) {
    @Json(ignored = true)
    val coordinates = WorldCoordinates.parseFromString(coordinatesString)
    @Json(ignored = true)
    val matchInput = GameInput(matchInputString)

    override fun toString() = "${coordinatesString}\n${matchInputString}"

    override fun equals(other: Any?): Boolean {
        return if(other is GameInput) {
            matchInput == other
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return coordinatesString.hashCode() * 31 + matchInputString.hashCode()
    }
}