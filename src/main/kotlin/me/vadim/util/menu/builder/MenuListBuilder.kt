package me.vadim.util.menu.builder

import me.vadim.util.menu.*
import me.vadim.util.menu.button.Button
import me.vadim.util.menu.impl.ListMenu
import me.vadim.util.menu.impl.MenuFill
import me.vadim.util.menu.impl.MenuSize
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

/**
 * Kotlin DSL builder [MenuList]. If you change anything from [MenuBuilder] in this scope, it will be reflected in the created [MenuList], but not modify the underlying `template`.
 *
 * @author vadim
 */
class MenuListBuilder<T>(
	plugin: Plugin,
	template: Menu,
	var items: MutableList<T> = mutableListOf(),
	var transformer: (T) -> ItemStack,
	var select: MenuList<T>.(InventoryClickEvent, Button, T) -> Unit = { _, _, _ -> },
	var page: MenuList<T>.(InventoryClickEvent, Button, Int, Int) -> Boolean = { _, _, _, _ -> true },
	var fill: MenuFill? = null
						) : MenuBuilder(plugin, template), FillHolder {

	constructor(plugin: Plugin, size: MenuSize, items: MutableList<T>, transformer: (T) -> ItemStack): this(plugin, menu(size.slots){}, items, transformer)

	/*
	 * Default buttons. You should change these manually.
	 */
	//ghetto item editing, i know. my builder is not present in this dependency for portability (i'm too lazy to put it in its own project)
	var next: Pair<Button, Int> = button(ItemStack(Material.ARROW).apply { editMeta {  it.setDisplayName(colorize("&cNext &l&m->")) } }) {} to 8
	var back: Pair<Button, Int> = button(ItemStack(Material.ARROW).apply { editMeta {  it.setDisplayName(colorize("&3&l&m<-&r&3 Back")) } }) {} to 0

	override fun build(): MenuList<T> = ListMenu(plugin, super.build(), transformer, fill, next, back, select, page, items)
}