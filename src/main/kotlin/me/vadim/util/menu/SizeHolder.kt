package me.vadim.util.menu

import me.vadim.util.menu.builder.MenuBuilder
import me.vadim.util.menu.impl.MenuFill
import me.vadim.util.menu.impl.MenuSize

/**
 * Interface to allow access to a [Menu]'s size within constructed [Menu] objects as well as within [MenuBuilder] scope lambdas.
 *
 * @author vadim
 */
interface SizeHolder {

	val size: MenuSize

}