package net.pirate_tales.util.menu.button.builder

import net.pirate_tales.util.menu.Menu
import net.pirate_tales.util.menu.button.Button
import net.pirate_tales.util.menu.button.impl.ButtonImpl
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Kotlin DSL builder for [Button].
 *
 * @author RuthlessJailer
 */
class ButtonBuilder(val item: ItemStack) {

	var protect = true
	var click: Menu.(InventoryClickEvent, Button) -> Unit = { _, _ -> }

	fun build() = ButtonImpl(item, protect, click)

}