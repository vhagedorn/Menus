package net.pirate_tales.util.menu

/**
 * @author RuthlessJailer
 */
class InnerClassTest(val string: String) {

	fun pr() = println(string)

	val inners = listOf(
		InnerClass(Math.random().toString()), InnerClass(" - " + Math.random().toString()))

	inner class InnerClass(val other: String) {

		fun printstr() = println(string + " ... "+ other)

	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val i1 = InnerClassTest("StringA")
			val i2 = InnerClassTest("StringB")

			i1.pr()
			i1.inners.forEach(InnerClass::printstr)

			i2.pr()
			i2.inners.forEach(InnerClass::printstr)
		}
	}


}