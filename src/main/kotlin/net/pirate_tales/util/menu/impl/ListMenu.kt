package net.pirate_tales.util.menu.impl

import net.pirate_tales.util.menu.Menu
import net.pirate_tales.util.menu.MenuList
import net.pirate_tales.util.menu.MenuListTitle
import net.pirate_tales.util.menu.button
import net.pirate_tales.util.menu.button.Button
import net.pirate_tales.util.menu.button.ButtonHolder
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.IllegalArgumentException

/**
 * @author RuthlessJailer
 */
class ListMenu<T>(
	plugin: Plugin,
	override val template: Menu,
	override val transformer: (T) -> ItemStack,
	fill: MenuFill?,
	override val next: Pair<Button, Int>,
	override val back: Pair<Button, Int>,
	override val select: MenuList<T>.(InventoryClickEvent, Button, T) -> Unit,
	override val page: MenuList<T>.(InventoryClickEvent, Button, Int, Int) -> Boolean,
	override val items: MutableList<T>
				 ) : BaseMenu(plugin, template), MenuList<T> {

	//todo: do we need a copy constructor here?

	override var fill = fill ?: exclude(*buttons.keys.toIntArray())
		private set

	/*private*/var pages = mutableListOf<PageMenu<T>>()

	override fun generate() {
		val s = fill.included.size
		val count = (items.size / s).coerceAtLeast(1)
		pages.clear()
		if(s > size.slots) throw IllegalArgumentException("included slots > menu size")
//		println("chunked.forEach")
//		items.chunked(s).forEach { println(it) }
//		println("mapIndexedTo")
//		items.chunked(s).mapIndexed { index, list ->
//			println("$index, $list")
//			null }
		items.chunked(s).mapIndexedTo(pages) { i, t ->
			println(i)
			val pm = PageMenu(t, i, count, this)
			buttons.forEach { (slot, button) ->
				println(slot.toString() + " -> " + button.item.type)
			}
			pm
		}
		if(pages.isEmpty()) {
			println("EMPTY")
			pages += PageMenu(emptyList(), 0, 1, this)
		}
		pages.forEach(Menu::generate)
	}

	override fun open(player: HumanEntity) = pages.first().open(player)

	fun open(player: HumanEntity, page: Int) = pages[page].open(player)

	/*private inner*/ class PageMenu<T>(items: List<T>, val i: Int, val count: Int, listMenu: ListMenu<T>) : BaseMenu(listMenu.plugin, listMenu.template) {
		private val nexti = (i + 1).coerceIn(0 until count)
		private val previ = (i - 1).coerceIn(0 until count)
		init {
			println("buttons ==== " + (this.buttons === listMenu.buttons))
			for ((i, item) in items.withIndex()) {//fill the buttons
				println("item:$item")
				button(listMenu.transformer(item)) {} into listMenu.fill.included[i]
//				val button = button(transformer(item)) {}
//				button.into(fill.included[i])
			}

			println("i:$i, next:$nexti, prev:$previ")

			//append our paging logic into the click callback for `back` and `next` buttons

			val b = listMenu.back.first.click
			button(listMenu.back.first.item) {
				click = { event, button ->
					b(this, event, button)
					println("$i -> ${i - 1}")
//					pages[i - 1].open(event.whoClicked)
					listMenu.pages[i - 1].open(event.whoClicked)
				}
			} into listMenu.back.second

			val n = listMenu.next.first.click
			button(listMenu.next.first.item) {
				click = { event, button ->
					n(this, event, button)
					println("$i -> ${i + 1}")
					listMenu.pages[i + 1].open(event.whoClicked)
				}
			} into listMenu.next.second

			//fill in title placeholders
//			title(title.replace(MenuListTitle.PAGE_CURRENT, (i + 1).toString()).replace(MenuListTitle.PAGE_COUNT, count.toString()))
//			title(title.replace(MenuListTitle.PAGE_CURRENT, i.toString()).replace(MenuListTitle.PAGE_COUNT, count.toString()))
			title(title.replace(MenuListTitle.PAGE_CURRENT, i.toString()))
		}

		override fun open(player: HumanEntity) {
			println("pg open")
			player.menuMeta(this)
			player.openInventory(inventory)
		}
	}

}