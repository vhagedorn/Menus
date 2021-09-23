package net.pirate_tales.util.menu.builder

import net.pirate_tales.util.menu.button.ButtonHolder
import net.pirate_tales.util.menu.Menu
import net.pirate_tales.util.menu.button.Button
import net.pirate_tales.util.menu.impl.BaseMenu
import net.pirate_tales.util.menu.impl.MenuSize
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.Plugin

/**
 * Kotlin DSL builder [Menu].
 *
 * @author RuthlessJailer
 */
open class MenuBuilder(
	val plugin: Plugin,
	val size: MenuSize,
	var parent: Menu? = null,
	var title: String = size.type?.defaultTitle ?: "",
	override val buttons: MutableMap<Int, Button> = mutableMapOf(),
	var previousMenuButton: Pair<Button, Int>? = null,
	var protectAll: Boolean = true,
	var open: Menu.(InventoryOpenEvent) -> Unit = { _ -> },
	var close: Menu.(InventoryCloseEvent) -> Unit = { _ -> },
	var click: Menu.(InventoryClickEvent, Button?) -> Boolean = { _, _ -> true },
					  ) : ButtonHolder {

	constructor(plugin: Plugin, templates: Menu): this(plugin,
													   templates.size, templates.parent, templates.title,
													   templates.buttons.toMutableMap(), templates.previousMenuButton?.copy(), templates.protectAll,
													   templates.open, templates.close, templates.click)

	open fun build(): Menu = BaseMenu(plugin, parent, size, title, buttons, previousMenuButton, protectAll, open, close, click)

}