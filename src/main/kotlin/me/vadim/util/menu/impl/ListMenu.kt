package me.vadim.util.menu.impl

import me.vadim.util.menu.Menu
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.MenuListTitle
import me.vadim.util.menu.button
import me.vadim.util.menu.button.Button
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.roundToInt

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

	final override var fill = fill ?: exclude(*buttons.keys.toIntArray())
		private set

	private var pages = mutableListOf<PageMenu>()

	override fun generate() {
		val s = fill.included.size
		val count = ceil(items.size.toDouble() / s.toDouble()).roundToInt().coerceAtLeast(1)
		pages.clear()
		if(s > size.slots) throw IllegalArgumentException("included slots > menu size")
		items.chunked(s).mapIndexedTo(pages) { i, t -> PageMenu(t, i, count) }
		if(pages.isEmpty()) pages += PageMenu(emptyList(), 0, 1)
		pages.forEach(Menu::generate)
	}

	override fun open(player: HumanEntity) = pages.first().open(player)

	private inner class PageMenu(items: List<T>, index: Int, count: Int) : BaseMenu(plugin, template) {

		init {
			for ((i, item) in items.withIndex()) {//fill the buttons
				button(transformer(item)) {
					click = { event, button ->
						select(event, button, items[i])
					}
				} into fill.included[i]
			}

			//append our paging logic into the click callback for `back` and `next` buttons

			val backCallback = back.first.click
			button(back.first.item) {
				click = { event, button ->
					val i = (index - 1).coerceIn(0 until count)
					if(page(event, button, index, i)) {
						backCallback(event, button)
						pages[i].open(event.whoClicked)
					}
				}
			} into back.second

			val nextCallback = next.first.click
			button(next.first.item) {
				click = { event, button ->
					val i = (index + 1).coerceIn(0 until count)
					if(page(event, button, index, i)) {
						nextCallback(event, button)
						pages[i].open(event.whoClicked)
					}
				}
			} into next.second

			//fill in title placeholders
			title(title.replace(MenuListTitle.PAGE_CURRENT, (index + 1).toString()).replace(MenuListTitle.PAGE_COUNT, count.toString()))
		}

	}

}