package me.vadim.util.menu.button.impl

import me.vadim.util.menu.Menu
import me.vadim.util.menu.button.Button
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * @author RuthlessJailer
 */
data class ButtonImpl(override var item: ItemStack, override val protect: Boolean, override val click: Menu.(InventoryClickEvent, Button) -> Unit) : Button