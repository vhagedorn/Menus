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
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class Menus : JavaPlugin() {

	companion object {
		@JvmStatic
		fun enable() = Bukkit.getServer().pluginManager.registerEvents(MenuListener, makesMeCry)
		@JvmStatic
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
 * Utility function to frame the outside of a [Menu] with `button`.
 */
fun MenuBuilder.frameWith(button: Button) {
	for (f in frame())
		button into f
}

/**
 * Utility function to return the indexes of an outer frame around the [Menu].
 */
fun MenuBuilder.frame(): IntArray {
	var frame = emptyArray<Int>()

	for (i in 0 until size.slots) {
		if (i < 9) frame += i//first row
		if ((i + 1) % 9 == 0 || i % 9 == 0) frame += i//sides (i or next multiple of 9)
		if (i >= size.slots - 9) frame += i//last row
	}

	return frame.toIntArray()
}

/**
 * @return the currently open [Menu] of this [HumanEntity], or `null` if one is not open
 */
fun HumanEntity.currentMenu(): Menu? = menu(MenuTags.CURRENT_MENU)

/**
 * @return the previous [Menu] of this [HumanEntity], or `null` if there is not one
 */
@Deprecated("This feature does not work.")
fun HumanEntity.previousMenu(): Menu? = null //menu(MenuTags.PREVIOUS_MENU)

/**
 * @param menu the new current menu
 * @return the [Menu] associated with the current `tag` on this [HumanEntity], or `null`
 */
/*internal*/ fun HumanEntity.menuMeta(menu: Menu?){
//	val current = currentMenu()
//
//	if (current != null) {//set the previous menu, if present
//		setMetadata(MenuTags.PREVIOUS_MENU, FixedMetadataValue(plugin, current))
//	}
//
	setMetadata(MenuTags.CURRENT_MENU, FixedMetadataValue(makesMeCry, menu))
}

/**
 * @param tag the metadata key from which to retrieve the menu
 * @return the [Menu] associated with the current `tag` on this [HumanEntity], or `null`
 */
/*internal*/ fun HumanEntity.menu(tag: String) =
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
	const val CURRENT_MENU  = "_MENU_now"
	@Deprecated("This feature does not work. Please use an account system for advanced menu tracking.")
	const val PREVIOUS_MENU = "_MENU_pre"
}

/**
 * [MenuList] title placeholders.
 */
object MenuListTitle {
	const val PAGE_CURRENT = "{PAGE}"
	@Deprecated("This feature was removed for dynamic menus. It functions identically as PAGE_CURRENT.", ReplaceWith("MenuListTitle.PAGE_CURRENT"))
	const val PAGE_COUNT = "{COUNT}"
}