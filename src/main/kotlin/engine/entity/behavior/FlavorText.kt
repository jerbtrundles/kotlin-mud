package engine.entity.behavior

object FlavorText {
    private val getItemStrings = arrayOf(
        "Well look at this.",
        "This looks useful.",
        "I'm taking this.",
        "This is mine now.",
        "Let's see how this works."
    )

    private val getValuableItemStrings = arrayOf(
        "Whoa! Look at this!",
        "This looks nice!",
    )

    private val chatterActionStrings = arrayOf(
        "randomName gazes up at the sky.",
        "randomName shuffles their feet.",
        "randomName glances around.",
        "randomName says \"quip\"",
        "randomName rummages around in their pockets, looking for something."
    )

    private val quips = arrayOf(
        "Nice weather today, isn't it?",
        "My feet ache somethin' awful.",
        "I'm a little sick. Don't get too close!",
        "I found a lucky coin on the ground the other day.",
        "Mrrrrrrr...."
    )

    fun get(type: EntityAction) =
        when (type) {
            EntityAction.GET_ANY_ITEM -> getItemStrings.random()
            EntityAction.GET_VALUABLE_ITEM -> getValuableItemStrings.random()
            EntityAction.CHATTER -> chatterActionStrings.random().replace("quip", quips.random())
            else -> ""
        }
}
