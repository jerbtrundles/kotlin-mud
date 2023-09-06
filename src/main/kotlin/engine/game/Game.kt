package engine.game

import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

object Game {
    var running = true
    fun println(str: String) {
        kotlin.io.println(str)
    }

    // all delays are split into smaller chunks
    const val microdelayDuration = 100

    suspend fun delayRandom(min: Int, max: Int) {
        val microdelays = Random.nextInt(min / microdelayDuration, max / microdelayDuration)

        repeat(microdelays) {
            if (running) {
                kotlinx.coroutines.delay(microdelayDuration.toLong())
            }
        }
    }

    // delay only if conditions are met
    suspend fun delayRandom(min: Int, max: Int, conditions: List<() -> Boolean>) {
        withTimeoutOrNull(Random.nextInt(min, max).toLong()) {
            while(true) {
                if(conditions.any {!it()}) {
                    return@withTimeoutOrNull
                }
                delay(100)
            }
        }
    }

    suspend fun delay(duration: Long) {
        val microdelays = (duration / microdelayDuration).toInt()

        repeat(microdelays) {
            if (running) {
                kotlinx.coroutines.delay(microdelayDuration.toLong())
            }
        }
    }
}
