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
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * @author vadim
 */
open class ListMenu<T>(
	plugin: Plugin,
	override val template: Menu,
	override val transformer: (T) -> ItemStack,
	fill: MenuFill?,
	override val next: Pair<Button, Int>,
	override val back: Pair<Button, Int>,
	override val select: MenuList<T>.(InventoryClickEvent, Button, T) -> Unit,
	override val page: MenuList<T>.(InventoryClickEvent, Button, Int, Int) -> Boolean,
	items: MutableList<T>
				 ) : BaseMenu(plugin, template), MenuList<T> {

	protected val itemsLock = Object()

	override var items: MutableList<T> = items
		set(value) {
			synchronized(itemsLock) {
				field = Collections.synchronizedList(value)
			}
		}

	//todo: do we need a copy constructor here?

	override var fill = fill ?: exclude(*buttons.keys.toIntArray())
		protected set

	protected var pages = mutableListOf<PageMenu>()

	override fun update() =
		pages.forEach {
			it.update()
		}

	override fun generate() {
		synchronized(itemsLock) {
			val s = fill.included.size
			val count = ceil(items.size.toDouble() / s.toDouble()).roundToInt().coerceAtLeast(1)

			if (s > size.slots) throw IllegalArgumentException("included slots > menu size")

			val items: List<List<T>> = items.chunked(s)

			items.forEachIndexed { i, t ->
				if (i >= pages.size) // lazily add pages
					pages += PageMenu(t, i, count)

				pages[i].items = t
				pages[i].count = count
			}

			if (pages.size > count) // lazily remove pages
				for (i in 0 until pages.size - count)
					pages -= pages[i + count].also { it.inventory.viewers.toSet().forEach { e -> open(e) } }

			if(this.items.isEmpty()) // fix: reset first page when items becomes empty
				pages.clear()
		}

		// instead of clearing, add or remove the right number of pages, then mutate the items after
		// this lets the client receive changes (it uses existing Menu#inventory objects)
		// the only downside of this is that MenuTitle#PAGE_COUNT is no longer supported, since the title can't be updated
//		pages.clear()
//		items.chunked(s).mapIndexedTo(pages) { i, t -> PageMenu(t, i, count) }
		if (pages.isEmpty()) pages += PageMenu(emptyList(), 0, 1)
		pages.forEach { it.generate() }
	}

	override fun open(player: HumanEntity) = pages.first().open(player)

	protected inner class PageMenu(var items: List<T>, private val index: Int, var count: Int) : BaseMenu(plugin, template) {

		override fun generate() {
			for (i in fill.included)//clear the page items, but don't replace any template buttons
				buttons -= i

			for ((i, item) in items.withIndex()) {//fill the buttons
				button(transformer(item)) {
					click = { event, button ->
						items.getOrNull(i)?.apply { select(event, button, this) }
					}
				} into fill.included[i]
			}

			//append our paging logic into the click callback for `back` and `next` buttons

			val backCallback = back.first.click
			button(back.first.item) {
				click = { event, button ->
					val i = (index - 1).coerceIn(0 until count)
					if (page(event, button, index, i)) {
						backCallback(event, button)
						pages[i].open(event.whoClicked)
					}
				}
			} into back.second

			val nextCallback = next.first.click
			button(next.first.item) {
				click = { event, button ->
					val i = (index + 1).coerceIn(0 until count)
					if (page(event, button, index, i)) {
						nextCallback(event, button)
						pages[i].open(event.whoClicked)
					}
				}
			} into next.second

			super.generate()
		}

		init {
			//fill in title placeholder
			title(
				title
					.replace(MenuListTitle.PAGE_CURRENT, (index + 1).toString())
					.replace(MenuListTitle.PAGE_CURRENT, (index + 1).toString())
				 )//title will not update unless the inventory is re-baked, but it won't since it's static for item refreshes
			generate()
		}

	}

}