package me.vadim.util.menu

import me.vadim.util.menu.impl.ListMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

/**
 * @author vadim
 */
object MenuListener : Listener {

	@EventHandler
	fun open(event: InventoryOpenEvent) {
		val menu = event.player.currentMenu() ?: return

		if (menu.inventory != event.inventory) return

		menu.open(menu, event)
	}

	@EventHandler
	fun close(event: InventoryCloseEvent) {
		val menu = event.player.currentMenu() ?: return

		if (menu.inventory != event.inventory) return

		menu.close(menu, event)
	}

	@EventHandler
	fun click(event: InventoryClickEvent) {
		val menu = event.whoClicked.currentMenu() ?: return
		val button = if (menu.inventory == event.clickedInventory) menu.buttons[event.slot] else null

		//check for correct view, but let clicks outside the menu still trigger #click
		if (event.view.topInventory != menu.inventory) return

		event.isCancelled = menu.protectAll
		if (menu.click(menu, event, button) && button != null) {
			event.isCancelled = button.protect
			button.click(menu, event, button)
		}
	}

}