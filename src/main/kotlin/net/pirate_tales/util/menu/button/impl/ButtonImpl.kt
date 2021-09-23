package net.pirate_tales.util.menu.button.impl

import net.pirate_tales.util.menu.Menu
import net.pirate_tales.util.menu.button.Button
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * @author RuthlessJailer
 */
data class ButtonImpl(override var item: ItemStack, override val protect: Boolean, override val click: Menu.(InventoryClickEvent, Button) -> Unit) : Button