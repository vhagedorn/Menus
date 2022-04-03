package me.vadim.util.menu.button

import me.vadim.util.menu.Menu
import me.vadim.util.menu.builder.MenuBuilder

/**
 * Interface to allow button creation within constructed [Menu] objects as well as within [MenuBuilder] scope lambdas.
 *
 * @author RuthlessJailer
 */
interface ButtonHolder {

	val buttons: MutableMap<Int, Button>

	infix fun Button.into(slots: IntRange) { slots.forEach { buttons[it] = this } }
	infix fun Button.into(slots: Array<Int>) = slots.forEach { buttons[it] = this }
	infix fun Button.into(slot: Int) { buttons[slot] = this }

}
