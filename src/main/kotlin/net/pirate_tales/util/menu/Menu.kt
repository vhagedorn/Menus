package net.pirate_tales.util.menu

import net.pirate_tales.util.menu.button.Button
import net.pirate_tales.util.menu.button.ButtonHolder
import net.pirate_tales.util.menu.impl.MenuSize
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

/**
 * Wrapper for [Inventory] (and its corresponding events), containing [Button]s and QoL methods to handle events, etc.
 *
 * @author RuthlessJailer
 */
interface Menu : ButtonHolder {


	/* Characteristics. */


	val parent: Menu?
	val size: MenuSize
	val title: String

	/**
	 * The button to open the previous [Menu].
	 * May be `null` to not exist.
	 * A [Pair] so that the slot can be specified (*with Kotlin's [to] function*) and accessed conveniently.
	 */
	val previousMenuButton: Pair<Button, Int>?

	/**
	 * Whether or not to protect all buttons by default (*this can be [overridden][Button.protect] on a per-button basis*).
	 * This is `true` by default.
	 */
	val protectAll: Boolean


	/* Event callbacks. */


	/**
	 * Called when this is [opened][open].
	 */
	val open: Menu.(InventoryOpenEvent) -> Unit

	/**
	 * Called when this is closed.
	 */
	val close: Menu.(InventoryCloseEvent) -> Unit

	/**
	 * Called when any slot is clicked. This is called before [Button.click], and will cancel the [Button.click] callback if `false` is returned.
	 *
	 * If no [Button] was clicked, then `null` will be passed instead.
	 */
	val click: Menu.(InventoryClickEvent, Button?) -> Boolean


	/* Convenience. */


	/**
	 * Only here for API purposes and you shouldn't rely on this for anything.
	 */
	val inventory: Inventory


	/* Methods. */


	/**
	 * Opens this menu for the provided [HumanEntity].
	 *
	 * @param player the player to open to
	 */
	fun open(player: HumanEntity)

	/**
	 * You can't easily update the title of an inventory. This method will create a new inventory and optionally reopen it for all current viewers.
	 *
	 * @param title the new title
	 * @param reopen whether or not to reopen the inventory to all current viewers
	 */
	fun title(title: String, reopen: Boolean = false)

	/**
	 * Shortcut for [generate] and [update].
	 */
	fun regen() {
		generate()
		update()
	}

	/**
	 * Update the inventory for its viewers. Call this after making changes to the inventory or calling [generate].
	 */
	fun update()

	/**
	 * Clears and then fills the inventory with buttons. In order to reflect changes client-side you must call [update].
	 */
	fun generate()

}