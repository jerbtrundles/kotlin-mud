package game

object Game {
    var running = true
    fun println(str: String) {
        kotlin.io.println(str)
    }

    suspend fun delay(millis: Int) {
        repeat(5) {
            if(running) {
                kotlinx.coroutines.delay(100)
            }
        }
    }
}
