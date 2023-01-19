import entity.EntityPosture
import game.GameActionType
import game.GameInput
import item.*
import world.World

object MyViewModel {
    var state = MyAppState(
        currentRegion = World.regions[0],
        currentSubregion = World.regions[0].subregions[0],
        currentRoom = World.regions[0].subregions[0].rooms[0]
    )

    val currentRoom
        get() = state.currentRoom
    private val currentSubregion
        get() = state.currentSubregion
    private val currentRegion
        get() = state.currentRegion

    // region move
    private fun doMove(gameInput: GameInput) {
        // directional move vs general connection
        val matchingConnection =
            state.currentRoom.connections.firstOrNull { connection -> connection.equals(gameInput) }
        matchingConnection?.run {
            val newRegion = World.regions[coordinates.region]
            val newSubregion = newRegion.subregions[coordinates.subregion]
            val newRoom = newSubregion.rooms[coordinates.room]
            state = MyAppState(newRegion, newSubregion, newRoom)
            doLook()
        } ?: doUnknown()
    }
    // endregion

    // region look
    private fun doLook(gameInput: GameInput? = null) {
        gameInput?.run {
            when (gameInput.words.size) {
                1 -> doLookCurrentRoom()
                2 -> doLookAtItemWithKeyword(gameInput.words[1])
                else -> when (words[1]) {
                    "in" -> doLookInItemWithKeyword(gameInput.suffixAfterWord(1))
                    "at" -> doLookAtItemWithKeyword(gameInput.suffixAfterWord(1))
                    else -> doUnknown()
                }
            }
        } ?: doLookCurrentRoom()
    }

    private fun doLookAtItemWithKeyword(word: String) {
        getItemWithKeyword(word)?.run {
            println(description)
        } ?: doUnknown()
    }

    private fun doLookInItemWithKeyword(word: String) {
        getTypedItemByKeyword<ItemContainer>(word)?.run {
            if (closed) {
                println("The $name is closed.")
            } else {
                println(inventoryString)
            }
        } ?: doUnknown()
    }

    private fun doLookCurrentRoom() {
        with(state) {
            println("[$currentRegion - $currentSubregion]")
            println(currentRoom)
        }
    }
    // endregion

    // region player posture
    private fun doStand() {
        if (Player.posture == EntityPosture.STANDING) {
            println("You're already standing.")
        } else {
            Player.posture = EntityPosture.STANDING
            println("You stand up.")
        }
    }

    private fun doSit() {
        if (Player.posture == EntityPosture.SITTING) {
            println("You're already sitting.")
        } else {
            Player.posture = EntityPosture.SITTING
            println("You sit down.")
        }
    }

    private fun doKneel() {
        if (Player.posture == EntityPosture.KNEELING) {
            println("You're already kneeling.")
        } else {
            Player.posture = EntityPosture.KNEELING
            println("You kneel.")
        }
    }
    // endregion

    // region item interactions
    private fun doPutItem(gameInput: GameInput) {
        // e.g. put sword in coffer
        if (gameInput.words[2] != "in") {
            doUnknown()
            return
        }

        val container = getTypedItemByKeyword<ItemContainer>(gameInput.words[3])
        container?.run {
            if (closed) {
                println("The $name is closed.")
            } else {
                 getItemWithKeyword(gameInput.words[1])?.run {
                    container.inventory.items.add(this)
                    Player.inventory.items.remove(this)
                    println("You put the $name into the ${container.name}.")
                } ?: doUnknown()
            }
        } ?: doUnknown()
    }

    private fun doDropItem(gameInput: GameInput) {
        Player.inventory.getItemByKeyword(gameInput.suffix)?.run {
            Player.inventory.items.remove(this)
            currentRoom.inventory.items.add(this)
            println("You drop $nameWithIndefiniteArticle.")
        } ?: doUnknown()
    }

    private fun doGetItemFromRoom(gameInput: GameInput) {
        currentRoom.inventory.getItemByKeyword(gameInput.suffix)?.run {
            Player.inventory.items.add(this)
            currentRoom.inventory.items.remove(this)
            println("You pick up $nameWithIndefiniteArticle.")
        } ?: doUnknown()
    }

    private fun doGetItemFromContainer(gameInput: GameInput) {
        // e.g. take potion from backpack
        if (gameInput.words[2] != "from") {
            doUnknown()
            return
        }

        val container = getTypedItemByKeyword<ItemContainer>(gameInput.words[3])
        container?.run {
            inventory.getItemByKeyword(gameInput.words[1])?.run {
                Player.inventory.items.add(this)
                container.inventory.items.remove(this)
                println("You take the $name from the ${container.name}.")
            } ?: doUnknown()
        } ?: doUnknown()
    }

    private fun doGetItem(gameInput: GameInput) {
        when (gameInput.words.size) {
            1 -> println("Get what?")
            2 -> doGetItemFromRoom(gameInput)
            4 -> doGetItemFromContainer(gameInput)
            else -> doUnknown()
        }
    }
    // endregion

    // region consumables
    private fun doEat(gameInput: GameInput) {
        getTypedItemByKeyword<ItemFood>(gameInput.suffix)?.run {
            if (--bites == 0) {
                println("You take a bite of your $name. That was the last of it.")
                Player.inventory.items.remove(this)
            } else {
                println(
                    "You take a bite of your $name. You have $bites " +
                            if (bites > 1) {
                                "bites"
                            } else {
                                "bite"
                            } + " left."
                )
            }
        } ?: doUnknown()
    }

    private fun doDrink(gameInput: GameInput) {
        getTypedItemByKeyword<ItemDrink>(gameInput.suffix)?.run {
            if (--quaffs == 0) {
                println("You sip from your $name. That was the last of it.")
                Player.inventory.items.remove(this)
            } else {
                println(
                    "You sip from your $name. You have $quaffs " +
                            if (quaffs > 1) {
                                "sips"
                            } else {
                                "sip"
                            } + " left."
                )
            }
        } ?: doUnknown()
    }
    // endregion

    // region containers
    private fun doOpenContainer(gameInput: GameInput) {
        getTypedItemByKeyword<ItemContainer>(gameInput.suffix)?.run {
            if (closed) {
                closed = false
                println("You open the $name.")
            } else {
                println("The $name is already open.")
            }
        } ?: doUnknown()
    }

    private fun doCloseContainer(gameInput: GameInput) {
        getTypedItemByKeyword<ItemContainer>(gameInput.suffix)?.run {
            if (closed) {
                println("The $name is already closed.")
            } else {
                closed = true
                println("You close the $name.")
            }
        } ?: doUnknown()
    }
    // endregion

    // region single-line handlers
    private fun doShowGold() = println(Player.goldString)
    private fun doShowInventory() = println(Player.inventoryString)
    private fun doShowHealth() = println(Player.healthString)
    private fun doUnknown() = println("I don't know, boss. Try something else.")
    // endregion

    // region inventory helpers
    private fun getItemWithKeyword(word: String): ItemBase? {
        return Player.inventory.getItemByKeyword(word)
            ?: currentRoom.inventory.getItemByKeyword(word)
    }

    private inline fun <reified T> getTypedItemByKeyword(word: String): T? {
        return Player.inventory.getTypedItemByKeyword<T>(word)
            ?: currentRoom.inventory.getTypedItemByKeyword<T>(word)
    }
    // endregion

    // region equip/unequip
    private fun doShowEquipment(gameInput: GameInput) {
        if (Player.weapon != null) {
            println("You have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            println("You don't have a weapon equipped.")
        }

        if (Player.armor != null) {
            println("You have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            println("You don't have any armor equipped.")
        }
    }
    private fun doRemoveEquipment(gameInput: GameInput) {
        if (Player.weapon != null && Player.weapon!!.keywords.contains(gameInput.words[1])) {
            val weapon = Player.weapon!!
            Player.inventory.items.add(weapon)
            Player.weapon = null
            println("You remove the ${weapon.name}.")
        } else if (Player.armor != null && Player.armor!!.keywords.contains(gameInput.words[1])) {
            val armor = Player.armor!!
            Player.inventory.items.add(armor)
            Player.armor = null
            println("You remove the ${armor.name}.")
        } else {
            doUnknown()
        }
    }

    private fun doEquipItem(gameInput: GameInput) {
        // find weapon from player inventory
        Player.inventory.getTypedItemByKeyword<ItemWeapon>(gameInput.words[1])?.run {
            doEquipWeaponFromPlayerInventory(this)
            // find weapon from current room
        } ?: currentRoom.inventory.getTypedItemByKeyword<ItemWeapon>(gameInput.words[1])?.run {
            doEquipWeaponFromCurrentRoom(this)
            // find armor from player inventory
        } ?: Player.inventory.getTypedItemByKeyword<ItemArmor>(gameInput.words[1])?.run {
            doEquipArmorFromPlayerInventory(this)
            // find armor from current room
        } ?: currentRoom.inventory.getTypedItemByKeyword<ItemArmor>(gameInput.words[1])?.run {
            doEquipArmorFromCurrentRoom(this)
        } ?: doUnknown()
    }

    private fun doEquipWeaponFromCurrentRoom(weapon: ItemWeapon) {
        if (Player.weapon != null) {
            println("You already have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.weapon = weapon
            currentRoom.inventory.items.remove(weapon)
            println("You pick up and equip the ${weapon.name}.")
        }
    }

    private fun doEquipArmorFromCurrentRoom(armor: ItemArmor) {
        if (Player.armor != null) {
            println("You already have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.armor = armor
            currentRoom.inventory.items.remove(armor)
            println("You pick up and equip the ${armor.name}.")
        }
    }

    private fun doEquipWeaponFromPlayerInventory(weapon: ItemWeapon) {
        if (Player.weapon != null) {
            println("You already have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.weapon = weapon
            Player.inventory.items.remove(weapon)
            println("You equip the ${weapon.name} from your inventory.")
        }
    }

    private fun doEquipArmorFromPlayerInventory(armor: ItemArmor) {
        if (Player.armor != null) {
            println("You already have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.armor = armor
            Player.inventory.items.remove(armor)
            println("You equip the ${armor.name} from your inventory.")
        }
    }
    // endregion

    // region unimplemented
    private fun doSellItem(gameInput: GameInput) {
        println("sell item")
    }

    private fun doSearch(gameInput: GameInput) {
        println("search")
    }

    private fun doPriceItem(gameInput: GameInput) {
        println("price item")
    }

    private fun doBuyItem(gameInput: GameInput) {
        println("buy item")
    }

    private fun doAttack(gameInput: GameInput) {
        println("attack")
    }
// endregion

    fun onInput(input: String) {
        val gameInput = GameInput(input)

        when (gameInput.action) {
            GameActionType.ATTACK -> doAttack(gameInput)
            GameActionType.EQUIP_ITEM -> doEquipItem(gameInput)
            GameActionType.REMOVE_EQUIPMENT -> doRemoveEquipment(gameInput)

            GameActionType.BUY_ITEM -> doBuyItem(gameInput)
            GameActionType.SELL_ITEM -> doSellItem(gameInput)

            GameActionType.GET_ITEM -> doGetItem(gameInput)
            GameActionType.DROP_ITEM -> doDropItem(gameInput)
            GameActionType.PUT_ITEM -> doPutItem(gameInput)

            GameActionType.EAT -> doEat(gameInput)
            GameActionType.DRINK -> doDrink(gameInput)

            GameActionType.OPEN_CONTAINER -> doOpenContainer(gameInput)
            GameActionType.CLOSE_CONTAINER -> doCloseContainer(gameInput)

            GameActionType.SIT -> doSit()
            GameActionType.STAND -> doStand()
            GameActionType.KNEEL -> doKneel()

            GameActionType.LOOK -> doLook(gameInput)
            GameActionType.MOVE -> doMove(gameInput)

            GameActionType.PRICE_ITEM -> doPriceItem(gameInput)

            GameActionType.SEARCH -> doSearch(gameInput)
            GameActionType.SHOW_GOLD -> doShowGold()
            GameActionType.SHOW_EQUIPMENT -> doShowEquipment(gameInput)
            GameActionType.SHOW_HEALTH -> doShowHealth()
            GameActionType.SHOW_INVENTORY -> doShowInventory()
            GameActionType.QUIT -> {}
            else -> doUnknown()
        }
    }
}