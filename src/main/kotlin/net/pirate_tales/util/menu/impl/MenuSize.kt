package net.pirate_tales.util.menu.impl

import net.pirate_tales.util.menu.colorize
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

/**
 * A class that functions as an either/or between [InventoryType] and slot count.
 *
 * @author RuthlessJailer
 */
class MenuSize private constructor(val type: InventoryType?, val slots: Int) {
	constructor(type: InventoryType) : this(type, type.defaultSize)
	constructor(size: Int) : this(null, size)

	fun toInventory(title: String? = type?.defaultTitle): Inventory =
		if (title == null) {
			if (type == null) Bukkit.createInventory(null, slots) else Bukkit.createInventory(null, type)
		} else {
			if (type == null) Bukkit.createInventory(null, slots, colorize(title)) else Bukkit.createInventory(null, type, colorize(title))
		}
}