package me.vadim.util.menu.impl

import me.vadim.util.menu.*
import me.vadim.util.menu.button.Button
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

/**
 * @author vadim
 */
open class BaseMenu(
	protected val plugin: Plugin,
	override val parent: Menu?, final override val size: MenuSize, final override var title: String,
	buttons: MutableMap<Int, Button>,
	override val previousMenuButton: Pair<Button, Int>?,
	override val protectAll: Boolean = true,
	override val open: Menu.(InventoryOpenEvent) -> Unit = { _ -> },
	override val close: Menu.(InventoryCloseEvent) -> Unit = { _ -> },
	override val click: Menu.(InventoryClickEvent, Button?) -> Boolean = { _, _ -> true }
			  ) : Menu {

	private val buttonLock = Object()

	constructor(plugin: Plugin, template: Menu) : this(
		plugin,
		template.parent, template.size, template.title,
		template.buttons.toMutableMap(), template.previousMenuButton?.copy(), template.protectAll,
		template.open, template.close, template.click
													  )

	override val buttons = ConcurrentHashMap(buttons)

	override var inventory: Inventory = size.toInventory(colorize(title))

	override fun open(player: HumanEntity) {
		player.menuMeta(this)
		player.openInventory(inventory)
	}

	override fun title(title: String, reopen: Boolean) {
		this.title = title
		inventory = size.toInventory(title)
		//generate()//don't call this from that method; it creates a new inventory anyway // what the huh
		if (reopen) inventory.viewers.forEach { entity -> open(entity) }
	}

	override fun update() {
		inventory.viewers.forEach { entity -> if (entity is Player) entity.updateInventory() }
	}

	override fun generate() {
		inventory.clear()

		synchronized(buttonLock) {
			val back = previousMenuButton
			if (back != null) {
				button(back.first.item) {//register back button
					click = { event, button ->
						back.first.click(this, event, button)
						parent?.open(event.whoClicked as Player)
					}
				} into back.second
			}

			buttons.forEach { (slot, button) ->
				inventory.setItem(slot, button.item)
			}
		}
	}
}