package net.pirate_tales.util.menu.impl

/**
 * A class that allows for the "inversion" of [IntArray]s. Reduces time when specifying slot includes for [ListMenu].
 *
 * @see ListMenu
 * @author RuthlessJailer
 */
class MenuFill private constructor(val bound: IntRange, val included: IntArray, val excluded: IntArray){
	companion object {
		fun included(bound: IntRange, vararg included: Int) = MenuFill(bound, included.constrain(bound), included.invert(bound))

		fun excluded(bound: IntRange, vararg excluded: Int) = MenuFill(bound, excluded.invert(bound), excluded.constrain(bound))

		private fun IntArray.invert(bound: IntRange): IntArray {
			val range = mutableSetOf<Int>()

			for(b in bound){//fill `range` with all possible values of `bound`
				range += b
			}

			range.removeAll(constrain(bound).toList())//remove all of the original array, 'inverting' it

			return range.toIntArray()
		}

		fun IntArray.constrain(bound: IntRange): IntArray {
			val list = toMutableSet()
			for (a in this) {
				if(a < bound.first || a > bound.last)list.remove(a)
			}
			return list.toIntArray()
		}
	}
}