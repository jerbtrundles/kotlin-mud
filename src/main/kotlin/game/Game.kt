package game

import java.util.concurrent.atomic.AtomicReference

object Game {
    var running = true
    var buffer = StringBuilder() // AtomicReference<StringBuilder>()

    fun print(str: String) {
        println(str)
//        buffer = buffer.clear()
//        print(s)
//        val s = buffer.toString()
//        println()
//        println(str)
//        print(s)
//        buffer = StringBuilder(s)
    }
}