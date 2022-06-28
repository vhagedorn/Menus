/**
 * Utilities for the menu package.
 *
 * @author vadim
 */
package me.vadim.util.menu

import me.vadim.util.menu.builder.MenuBuilder
import me.vadim.util.menu.builder.MenuListBuilder
import me.vadim.util.menu.button.Button
import me.vadim.util.menu.button.ButtonHolder
import me.vadim.util.menu.button.builder.ButtonBuilder
import me.vadim.util.menu.impl.ListMenu
import me.vadim.util.menu.impl.MenuSize
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class Menus : JavaPlugin() {

	companion object {
		fun enable() = Bukkit.getServer().pluginManager.registerEvents(MenuListener, makesMeCry)
		fun disable() = Bukkit.getOnlinePlayers().forEach {
			it.removeMetadata(MenuTags.CURRENT_MENU, makesMeCry)
			it.removeMetadata(MenuTags.PREVIOUS_MENU, makesMeCry)
		}
	}

	//if we're loaded as a plugin, these will run
	//otherswise let's call the static methods with the shading plugin assigning `makesMeCry` onLoad
	override fun onLoad() {
		makesMeCry = this
	}

	override fun onEnable() = enable()

	override fun onDisable() = disable()

}

lateinit var makesMeCry: Plugin

fun colorize(string: String): String = ChatColor.translateAlternateColorCodes('&', string)

/**
 * Creates a [Menu].
 *
 * @param slots the size of the inventory
 * @param builder the DSL builder
 *
 * @return the built [Menu]
 */
inline fun menu(slots: Int, builder: MenuBuilder.() -> Unit): Menu =
	MenuBuilder(makesMeCry, MenuSize(slots)).apply(builder).build()

/**
 * Creates a [Menu].
 *
 * @param type the type of the inventory
 * @param builder the DSL builder
 *
 * @return the built [Menu]
 */
inline fun menu(type: InventoryType, builder: MenuBuilder.() -> Unit): Menu =
	MenuBuilder(makesMeCry, MenuSize(type)).apply(builder).build()

inline fun <T> menuListOf(slots: Int, items: Collection<T>, noinline transformer: (T) -> ItemStack, builder: MenuListBuilder<T>.() -> Unit): MenuList<T> =
	MenuListBuilder(makesMeCry, MenuSize(slots), items.toMutableList(), transformer).apply(builder).build()

inline fun <T> menuListOf(type: InventoryType, items: Collection<T>, noinline transformer: (T) -> ItemStack, builder: MenuListBuilder<T>.() -> Unit): MenuList<T> =
	MenuListBuilder(makesMeCry, MenuSize(type), items.toMutableList(), transformer).apply(builder).build()

/**
 * Converts a [Menu] to a [ListMenu], using the [Menu] as a template for each page.
 *
 */
inline fun <T> Menu.toList(items: Collection<T>, noinline transformer: (T) -> ItemStack, builder: MenuListBuilder<T>.() -> Unit) =
	MenuListBuilder(makesMeCry, this, items.toMutableList(), transformer).apply(builder).build()

/**
 * Creates a [Button] within a [ButtonHolder] scope.
 *
 * @param item the item which to display
 * @param builder the DSL builder
 *
 * @return the built [Button]
 */
inline fun ButtonHolder.button(item: ItemStack, builder: ButtonBuilder.() -> Unit): Button = ButtonBuilder(item).apply(builder).build()

/**
 * @return the currently open [Menu] of this [HumanEntity], or `null` if one is not open
 */
fun HumanEntity.currentMenu(): Menu? = menu(MenuTags.CURRENT_MENU)

/**
 * @return the previous [Menu] of this [HumanEntity], or `null` if there is not one
 */
fun HumanEntity.previousMenu(): Menu? = menu(MenuTags.PREVIOUS_MENU)

/**
 * @param tag the metadata key from which to retrieve the menu
 * @return the [Menu] associated with the current `tag` on this [HumanEntity], or `null`
 */
private fun HumanEntity.menu(tag: String) =
	if (!hasMetadata(tag)) {
		null
	} else {
		getMetadata(tag).firstOrNull()?.value() as Menu?
	}


/**
 * The maximum size of any [Inventory].
 */
const val INVENTORY_MAX_SIZE = 9 * 6

/**
 * Various metadata tags corresponding to menus.
 */
object MenuTags {
	const val CURRENT_MENU = "P-T_MENU_now"
	const val PREVIOUS_MENU = "P-T_MENU_last"
}

/**
 * [MenuList] title placeholders.
 */
object MenuListTitle {
	const val PAGE_CURRENT = "{PAGE}"
	@Deprecated("This feature was removed for dynamic menus. It functions identically as PAGE_CURRENT.", ReplaceWith("MenuListTitle.PAGE_CURRENT"))
	const val PAGE_COUNT = "{COUNT}"
}