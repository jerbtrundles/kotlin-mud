package engine.player

import debug.Debug
import engine.entity.EntityPosture
import engine.game.Game
import engine.game.GameInput
import engine.item.*
import engine.world.RoomBank
import engine.world.RoomShop
import engine.world.World

object MyViewModel {
    // region move
    private fun doMove(gameInput: GameInput) {
        // directional move vs general connection
        val matchingConnection =
            Player.currentRoom.connections.firstOrNull { connection -> connection.equals(gameInput) }
        matchingConnection?.run {
            Player.coordinates = coordinates
            doLook()
        } ?: doUnknown()
    }
    // endregion

    // region look
    private fun doLook(gameInput: GameInput? = null) {
        gameInput?.run {
            when (gameInput.words.size) {
                // "look"
                1 -> doLookCurrentRoom()
                // "look item"
                2 -> doLookAtItemWithKeyword(keyword = gameInput.words[1])
                // 3+ - "look at item", "look in item"
                else -> when (words[1]) {
                    "in" -> doLookInItemWithKeyword(keyword = gameInput.suffixAfterWord(1))
                    "at" -> doLookAtItemWithKeyword(keyword = gameInput.suffixAfterWord(1))
                    else -> doUnknown()
                }
            }
        } ?: doLookCurrentRoom()
    }

    private fun doLookAtItemWithKeyword(keyword: String) {
        getItemWithKeyword(keyword)?.run {
            Game.println(description)
        } ?: doUnknown()
    }

    private fun doLookInItemWithKeyword(keyword: String) {
        getTypedItemByKeyword<ItemContainer>(keyword)?.run {
            if (closed) {
                Game.println("The $name is closed.")
            } else {
                Game.println(inventoryString)
            }
        } ?: doUnknown()
    }

    private fun doLookCurrentRoom() {
        with(Player.coordinates) {
            val region = World.regions[region]
            val subregion = region.subregions[subregion]
            val room = subregion.rooms[room]

            engine.game.Game.println("[$region - $subregion]")
            engine.game.Game.println(room.displayString())
        }
    }
    // endregion

    // region player posture
    private fun doStand() {
        if (Player.posture == EntityPosture.STANDING) {
            Game.println("You're already standing.")
        } else {
            Player.posture = EntityPosture.STANDING
            Game.println("You stand up.")
        }
    }

    private fun doSit() {
        if (Player.posture == EntityPosture.SITTING) {
            Game.println("You're already sitting.")
        } else {
            Player.posture = EntityPosture.SITTING
            Game.println("You sit down.")
        }
    }

    private fun doKneel() {
        if (Player.posture == EntityPosture.KNEELING) {
            Game.println("You're already kneeling.")
        } else {
            Player.posture = EntityPosture.KNEELING
            Game.println("You kneel.")
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
                Game.println("The $name is closed.")
            } else {
                getItemWithKeyword(gameInput.words[1])?.run {
                    container.inventory.items.add(this)
                    Player.inventory.items.remove(this)
                    Game.println("You put the $name into the ${container.name}.")
                } ?: doUnknown()
            }
        } ?: doUnknown()
    }

    private fun doDropItem(gameInput: GameInput) {
        Player.inventory.getItemByKeyword(gameInput.suffix)?.run {
            Player.inventory.items.remove(this)
            Player.currentRoom.inventory.items.add(this)
            Game.println("You drop $nameWithIndefiniteArticle.")
        } ?: doUnknown()
    }

    private fun doGetItemFromRoom(gameInput: GameInput) {
        Player.currentRoom.inventory.getItemByKeyword(gameInput.suffix)?.run {
            Player.inventory.items.add(this)
            Player.currentRoom.inventory.items.remove(this)
            Game.println("You pick up $nameWithIndefiniteArticle.")
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
                Game.println("You take the $name from the ${container.name}.")
            } ?: doUnknown()
        } ?: doUnknown()
    }

    private fun doGetItem(gameInput: GameInput) {
        when (gameInput.words.size) {
            1 -> Game.println("Get what?")
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
                Game.println("You take a bite of your $name. That was the last of it.")
                Player.inventory.items.remove(this)
            } else {
                Game.println(
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
                Game.println("You sip from your $name. That was the last of it.")
                Player.inventory.items.remove(this)
            } else {
                Game.println(
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
                Game.println("You open the $name.")
            } else {
                Game.println("The $name is already open.")
            }
        } ?: doUnknown()
    }

    private fun doCloseContainer(gameInput: GameInput) {
        getTypedItemByKeyword<ItemContainer>(gameInput.suffix)?.run {
            if (closed) {
                Game.println("The $name is already closed.")
            } else {
                closed = true
                Game.println("You close the $name.")
            }
        } ?: doUnknown()
    }
    // endregion

    // region single-line handlers
    private fun doShowBankAccountBalance() = Game.println(Player.bankAccountBalanceString)
    private fun doShowGold() = Game.println(Player.goldString)
    private fun doShowInventory() = Game.println(Player.inventoryString)
    private fun doShowHealth() = Game.println(Player.healthString)
    private fun doUnknown() = Game.println("I don't know, boss. Try something else.")
    // endregion

    // region inventory helpers
    private fun getItemWithKeyword(keyword: String): ItemBase? =
        Player.inventory.getItemByKeyword(keyword)
            ?: Player.currentRoom.inventory.getItemByKeyword(keyword)

    private inline fun <reified T> getTypedItemByKeyword(word: String): T? =
        Player.inventory.getTypedItemByKeyword<T>(word)
            ?: Player.currentRoom.inventory.getTypedItemByKeyword<T>(word)
    // endregion

    // region equip/unequip
    private fun doShowEquipment() {
        if (Player.weapon != null) {
            Game.println("You have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Game.println("You don't have a weapon equipped.")
        }

        if (Player.armor != null) {
            Game.println("You have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Game.println("You don't have any armor equipped.")
        }
    }

    private fun doRemoveEquipment(gameInput: GameInput) {
        if (Player.weapon != null && Player.weapon!!.keywords.contains(gameInput.words[1])) {
            val weapon = Player.weapon!!
            Player.inventory.items.add(weapon)
            Player.weapon = null
            Game.println("You remove the ${weapon.name}.")
        } else if (Player.armor != null && Player.armor!!.keywords.contains(gameInput.words[1])) {
            val armor = Player.armor!!
            Player.inventory.items.add(armor)
            Player.armor = null
            Game.println("You remove the ${armor.name}.")
        } else {
            doUnknown()
        }
    }

    private fun doEquipItem(gameInput: GameInput) {
        // find weapon from player inventory
        Player.inventory.getTypedItemByKeyword<ItemWeapon>(gameInput.words[1])?.run {
            doEquipWeaponFromPlayerInventory(this)
            // find weapon from current room
        } ?: Player.currentRoom.inventory.getTypedItemByKeyword<ItemWeapon>(gameInput.words[1])?.run {
            doEquipWeaponFromCurrentRoom(this)
            // find armor from player inventory
        } ?: Player.inventory.getTypedItemByKeyword<ItemArmor>(gameInput.words[1])?.run {
            doEquipArmorFromPlayerInventory(this)
            // find armor from current room
        } ?: Player.currentRoom.inventory.getTypedItemByKeyword<ItemArmor>(gameInput.words[1])?.run {
            doEquipArmorFromCurrentRoom(this)
        } ?: doUnknown()
    }

    private fun doEquipWeaponFromCurrentRoom(weapon: ItemWeapon) {
        if (Player.weapon != null) {
            Game.println("You already have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.weapon = weapon
            Player.currentRoom.inventory.items.remove(weapon)
            Game.println("You pick up and equip the ${weapon.name}.")
        }
    }

    private fun doEquipArmorFromCurrentRoom(armor: ItemArmor) {
        if (Player.armor != null) {
            Game.println("You already have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.armor = armor
            Player.currentRoom.inventory.items.remove(armor)
            Game.println("You pick up and equip the ${armor.name}.")
        }
    }

    private fun doEquipWeaponFromPlayerInventory(weapon: ItemWeapon) {
        if (Player.weapon != null) {
            Game.println("You already have ${Player.weapon!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.weapon = weapon
            Player.inventory.items.remove(weapon)
            Game.println("You equip the ${weapon.name} from your inventory.")
        }
    }

    private fun doEquipArmorFromPlayerInventory(armor: ItemArmor) {
        if (Player.armor != null) {
            Game.println("You already have ${Player.armor!!.nameWithIndefiniteArticle} equipped.")
        } else {
            Player.armor = armor
            Player.inventory.items.remove(armor)
            Game.println("You equip the ${armor.name} from your inventory.")
        }
    }
    // endregion

    // region attack/search entities
    private fun doAttack(gameInput: GameInput) {
        val entity = Player.currentRoom.findLivingMonster(gameInput.suffix)

        entity?.run {
            val weapon = Player.weapon?.name ?: "fists"
            val attack = attributes.strength + (Player.weapon?.power ?: 0)
            val defense = attributes.baseDefense
            val damage = (attack - defense).coerceAtLeast(0)

            println("You swing at the $name with your $weapon.")

            if (damage > 0) {
                println("You hit for $damage damage.")
            } else {
                println("You miss!")
            }

            attributes.currentHealth -= damage
            if (attributes.currentHealth <= 0) {
                Player.experience += experience

                println("The $name dies.")
                println("You've gained $experience experience.")
            }
        } ?: doUnknown()
    }

    private fun doSearch(gameInput: GameInput) {
        Player.currentRoom.findDeadAndUnsearchedMonster(gameInput.suffix)?.let { deadMonster ->

            Player.currentRoom.announce("You search the ${deadMonster.name}.")

            println("You find ${deadMonster.gold} gold on the ${deadMonster.name}.")
            Player.gold += deadMonster.gold
            println(Player.goldString)

            if (deadMonster.inventory.items.isNotEmpty()) {
                Player.currentRoom.inventory.items.addAll(deadMonster.inventory.items)
                Player.currentRoom.announce("The ${deadMonster.name} drops ${deadMonster.inventory.collectionString}.")
            }

            deadMonster.hasNotBeenSearched = false
        } ?: doUnknown()
    }
    // endregion

    // region shops
    private fun doSellItem(gameInput: GameInput) {
        (Player.currentRoom as? RoomShop)?.run {
            Player.inventory.getItemByKeyword(gameInput.suffix)?.run {
                Player.inventory.items.remove(this)
                Player.gold += sellValue
                println("You sell your $name to the merchant and receive $sellValue gold.")
                println(Player.goldString)
            } ?: doUnknown()
        } ?: doRoomIsNotShop()
    }

    private fun doListItems() {
        (Player.currentRoom as? RoomShop)?.run {
            val sb = StringBuilder()
            sb.appendLine("This shop has the following items for sale:")
            soldItemTemplates.forEach { template ->
                sb.appendLine(template.shopItemString)
            }
            println(sb.toString())
        } ?: doRoomIsNotShop()
    }

    private fun doRoomIsNotShop() {
        println("You don't see a merchant anywhere around here.")
    }

    private fun doRoomIsNotBank() {
        println("You don't see a bank teller anywhere around here.")
    }

    private fun doPriceItem(gameInput: GameInput) {
        (Player.currentRoom as? RoomShop)?.run {
            Player.inventory.getItemByKeyword(gameInput.suffix)?.run {
                println("You can sell the $name here for $sellValue gold.")
            } ?: doUnknown()
        } ?: doRoomIsNotShop()
    }

    private fun doBuyItem(gameInput: GameInput) {
        (Player.currentRoom as? RoomShop)?.run {
            soldItemTemplates.firstOrNull { template ->
                template.matches(gameInput.suffix)
            }?.run {
                if (Player.gold >= value) {
                    val item = createItem()
                    Player.gold -= item.value
                    println("You purchase ${item.nameWithIndefiniteArticle} from the merchant for ${item.value} gold.")
                    println(Player.goldString)
                    Player.inventory.items.add(item)
                } else {
                    println("You don't have enough gold (${Player.gold}) to buy the $name ($value).")
                }
            } ?: doUnknown()
        } ?: doRoomIsNotShop()
    }
    // endregion

    private fun doDepositMoney(gameInput: GameInput) {
        (Player.currentRoom as? RoomBank)?.run {
            when (gameInput.words.size) {
                1 -> doUnknown()
                else -> {
                    gameInput.words[1].toIntOrNull()?.let { depositAmount ->
                        if (depositAmount > Player.gold) {
                            println("You don't have that much gold to deposit.")
                        } else {
                            Player.bankAccountBalance += depositAmount
                            Player.gold -= depositAmount

                            println("You deposit $depositAmount gold.")
                            println(Player.bankAccountBalanceString)
                            println(Player.goldString)
                        }
                    } ?: doUnknown()
                }
            }
        } ?: doRoomIsNotBank()
    }

    private fun doWithdrawMoney(gameInput: GameInput) {
        (Player.currentRoom as? RoomBank)?.run {
            when (gameInput.words.size) {
                1 -> doUnknown()
                else -> {
                    gameInput.words[1].toIntOrNull()?.let { withdrawAmount ->
                        if (withdrawAmount > Player.bankAccountBalance) {
                            println("You don't have that much gold in your account to withdraw.")
                        } else {
                            Player.bankAccountBalance -= withdrawAmount
                            Player.gold += withdrawAmount

                            println("You withdraw $withdrawAmount gold.")
                            println(Player.bankAccountBalanceString)
                            println(Player.goldString)
                        }
                    } ?: doUnknown()
                }
            }
        } ?: doRoomIsNotBank()
    }

    private fun doCheckBankAccountBalance() {
        (Player.currentRoom as? RoomBank)?.run {
            println(Player.bankAccountBalanceString)
        } ?: doRoomIsNotBank()
    }

    private fun doAssess(gameInput: GameInput) {
        when (gameInput.words.size) {
            1 -> doUnknown()
            2 -> Player.currentRoom.findAnyMonster(gameInput.suffix)?.let {
                Debug.assessSituations(it)
            } ?: doUnknown()

            else -> doUnknown()
        }
    }

    fun onInput(input: String) = onInput(GameInput(input))
    fun onInput(gameInput: GameInput) {
        when (gameInput.action) {
            PlayerAction.ATTACK -> doAttack(gameInput)
            PlayerAction.EQUIP_ITEM -> doEquipItem(gameInput)
            PlayerAction.REMOVE_EQUIPMENT -> doRemoveEquipment(gameInput)

            PlayerAction.BUY_ITEM -> doBuyItem(gameInput)
            PlayerAction.SELL_ITEM -> doSellItem(gameInput)

            PlayerAction.GET_ITEM -> doGetItem(gameInput)
            PlayerAction.DROP_ITEM -> doDropItem(gameInput)
            PlayerAction.PUT_ITEM -> doPutItem(gameInput)

            PlayerAction.EAT -> doEat(gameInput)
            PlayerAction.DRINK -> doDrink(gameInput)

            PlayerAction.OPEN_CONTAINER -> doOpenContainer(gameInput)
            PlayerAction.CLOSE_CONTAINER -> doCloseContainer(gameInput)

            PlayerAction.SIT -> doSit()
            PlayerAction.STAND -> doStand()
            PlayerAction.KNEEL -> doKneel()

            PlayerAction.LOOK -> doLook(gameInput)
            PlayerAction.MOVE -> doMove(gameInput)

            PlayerAction.WITHDRAW_MONEY -> doWithdrawMoney(gameInput)
            PlayerAction.DEPOSIT_MONEY -> doDepositMoney(gameInput)
            PlayerAction.CHECK_BANK_ACCOUNT_BALANCE -> doCheckBankAccountBalance()

            PlayerAction.SEARCH -> doSearch(gameInput)
            PlayerAction.CHECK_GOLD -> doShowGold()
            PlayerAction.SHOW_EQUIPMENT -> doShowEquipment()
            PlayerAction.SHOW_HEALTH -> doShowHealth()
            PlayerAction.SHOW_INVENTORY -> doShowInventory()

            PlayerAction.LIST_ITEMS -> doListItems()
            PlayerAction.PRICE_ITEM -> doPriceItem(gameInput)

            // assess monster situations
            PlayerAction.ASSESS -> doAssess(gameInput)

            PlayerAction.QUIT -> Game.running = false

            PlayerAction.NONE -> doUnknown()

            else -> doUnknown()
        }
    }
}