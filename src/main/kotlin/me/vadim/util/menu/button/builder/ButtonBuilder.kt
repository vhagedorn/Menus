package me.vadim.util.menu.button.builder

import me.vadim.util.menu.Menu
import me.vadim.util.menu.button.Button
import me.vadim.util.menu.button.impl.ButtonImpl
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Kotlin DSL builder for [Button].
 *
 * @author vadim
 */
class ButtonBuilder(val item: ItemStack) {

	var protect = true
	var click: Menu.(InventoryClickEvent, Button) -> Unit = { _, _ -> }

	fun build() = ButtonImpl(item, protect, click)

}