package net.pirate_tales.util.menu

import net.pirate_tales.util.menu.impl.MenuFill
import net.pirate_tales.util.menu.builder.MenuListBuilder

/**
 * Interface to allow [MenuFill] creation within constructed [MenuList] objects as well as within [MenuListBuilder] scope lambdas.
 *
 * @author RuthlessJailer
 */
interface FillHolder {
	fun exclude(vararg slots: Int) = MenuFill.excluded(0 until INVENTORY_MAX_SIZE, *slots)
	fun exclude(slots: IntRange) = MenuFill.excluded(0 until INVENTORY_MAX_SIZE, *slots.toList().toIntArray())
	fun include(vararg slots: Int) = MenuFill.included(0 until INVENTORY_MAX_SIZE, *slots)
	fun include(slots: IntRange) = MenuFill.included(0 until INVENTORY_MAX_SIZE, *slots.toList().toIntArray())
}