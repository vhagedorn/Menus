package me.vadim.util.menu.impl

/**
 * A class that allows for the "inversion" of [IntArray]s. Reduces time when specifying slot includes for [ListMenu].
 *
 * @see ListMenu
 * @author vadim
 */
class MenuFill private constructor(val bound: IntRange, val included: IntArray, val excluded: IntArray){
	companion object {
		fun included(bound: IntRange, vararg included: Int) = MenuFill(bound, included.constrain(bound), included.invert(bound))
		fun excluded(bound: IntRange, vararg excluded: Int) = MenuFill(bound, excluded.invert(bound), excluded.constrain(bound))

		private fun IntArray.invert(bound: IntRange): IntArray = bound.toMutableList().also { it.removeAll(this.toList()) }.toIntArray().constrain(bound)

		fun IntArray.constrain(bound: IntRange): IntArray {
			val list = toMutableSet()
			for (a in this)
				if (a < bound.first || a > bound.last) list -= a
			return list.toIntArray()
		}
	}
}