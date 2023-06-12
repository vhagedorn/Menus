package me.vadim.util.menu

import me.vadim.util.menu.impl.MenuFill
import me.vadim.util.menu.builder.MenuListBuilder

/**
 * Interface to allow [MenuFill] creation within constructed [MenuList] objects as well as within [MenuListBuilder] scope lambdas.
 *
 * @author vadim
 */
interface FillHolder : SizeHolder {
	fun exclude(vararg slots: Int) = MenuFill.excluded(0 until size.slots, *slots)
	fun exclude(vararg slots: IntProgression) = MenuFill.excluded(0 until size.slots, *slots.map { it.toList().toIntArray() }.flatMap { it.toList() }.toIntArray())
	fun include(vararg slots: Int) = MenuFill.included(0 until size.slots, *slots)
	fun include(vararg slots: IntProgression) = MenuFill.included(0 until size.slots, *slots.map { it.toList().toIntArray() }.flatMap { it.toList() }.toIntArray())
}