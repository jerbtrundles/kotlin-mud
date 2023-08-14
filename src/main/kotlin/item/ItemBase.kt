package item

import isVowel

abstract class ItemBase(
    val name: String,
    val description: String,
    val weight: Double,
    val value: Int,
    val keywords: List<String>
) {
    val sellValue = (value / 2).coerceAtLeast(1)
    val nameWithIndefiniteArticle = if(isVowel(name[0])) { "an $name" } else { "a $name" }
}