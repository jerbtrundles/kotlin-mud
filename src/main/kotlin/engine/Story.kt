package engine

import engine.entity.EntityBase
import engine.game.Game

class Story(
    val parts: List<String>,
    val partCooldown: Long = 3000L,
    val storyCooldown: Long = 15000L
) {
    private var index = 0

    suspend fun play(speaker: EntityBase) {
        while(!isDone()) {
            playNext(speaker)
            Game.delay(partCooldown)
        }
    }

    private fun playNext(speaker: EntityBase) {
        if (!isDone()) {
            Game.println("${speaker.nameForStory} says \"${parts[index++]}\"")
        }
    }

    private fun isDone() = index == parts.count()

    companion object {
        val default = Story(
            listOf(
                "Now this is a story all about how.",
                "My life got flipped turned upside down.",
                "And I'd like to take a minute, just sit right there.",
                "I'll tell you how I became the prince of a town called Bel-Air."
            )
        )
    }
}