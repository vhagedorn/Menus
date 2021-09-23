package net.pirate_tales.util.menu

import net.pirate_tales.util.menu.impl.ListMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

/**
 * @author RuthlessJailer
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
		val button = menu.buttons[event.slot]

		if (menu.inventory != event.inventory) return

		if(menu is ListMenu.PageMenu<*>){
			println("page")
		}


		event.isCancelled = menu.protectAll
		if (menu.click(menu, event, button) && button != null) {
			event.isCancelled = button.protect
			button.click(menu, event, button)
		}
	}

}