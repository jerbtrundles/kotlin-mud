package engine.game

import engine.player.PlayerAction

class GameInput constructor(
    private val rawInput: String
) {
    private val sanitizedInput = if (MovementDirection.isDirectionalWord(rawInput)
        || (rawInput.startsWith("go ") && MovementDirection.isDirectionalWord(rawInput.substringAfter(' ')))
    ) {
        val direction = rawInput.removePrefix("go ")
        "go " + MovementDirection.parseFromString(direction).name.lowercase()
    } else {
        rawInput.lowercase()
    }

    val suffix = sanitizedInput.substringAfter(' ')
    val words = sanitizedInput.split(' ', ignoreCase = true)

    val action = if (MovementDirection.isDirectionalWord(rawInput)) {
        PlayerAction.MOVE
    } else {
        PlayerAction.fromString(words[0])
    }

    override fun equals(other: Any?): Boolean {
        return if(other is GameInput) {
            sanitizedInput == other.sanitizedInput
        } else {
            super.equals(other)
        }
    }

    override fun hashCode() = sanitizedInput.hashCode()
    override fun toString() = rawInput
    fun toDebugString() = "Words: ${words.joinToString()}\nAction: $action"
    fun suffixAfterWord(index: Int): String {
        // look at backpack
        // words.size == 3
        // suffixAfterWord(0) == "at backpack"
        // suffixAfterWord(1) == "backpack"
        // suffixAfterWord(2) == "" (fails)
        // suffix after 1 is backpack
        // if words.size (3) < index (2)
        return if(words.size < index) {
            ""
        } else {
            val words = words.subList(index + 1, words.size)
            words.joinToString(" ")
        }
    }
}