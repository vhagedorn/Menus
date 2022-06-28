# Menus
A Kotlin DSL GUI lib

## Installation

Clone and publish.

Bash (basically the same on cmd):
```bash
git clone https://github.com/RuthlessJailer/Menus/
cd Menus
gradle init
./gradlew publishToMavenLocal
```

## Simple usage

### Setup

#### As a plugin (.jar in server plugins folder)

No extra setup needed, simply use at will.

#### Shaded

Setup (register Bukkit event handlers and singelton instance for tasks):
```java

  //assign singeton instance
	@Override
	public void onLoad() {
		MenusKt.makesMeCry = this;
	}

  //call enable (registers listeners)
	@Override
	public void onEnable() {
		Menus.Companion.enable();
    //...
	}
  
  //call disable (removes metadata)
	@Override
	public void onDisable() {
    //...
		Menus.Companion.disable();
	}

```

### Creating a menu

After you create one, make sure to call `#regen` before you send it to the player, and every time you make changes to the inventory or button layout (or `#items`, etc.).

There are 2 builder methods, `#menuListOf` and `#menu`.

Basically, there is DSL sugar indise the builder blocks, briefly explained:

```kt

menu(/*size or InventoryType*/) {
  button(/* ItemStack */){
    click = { event, button -> 
      //button was clicked
    }
  } into SLOT //or array of slots
  
  title = "Name Me"
  
  //there are other callbacks and fields to set here, some are demoed in the longer snippet below 
} 

menuListOf(items, size) {
  //all the same functionality, PLUS
  // back & next buttons
  // fill (included or excluded slots for the `items` collection)
}

```


`#menu` demo (there are some helper methods, but they all accomplish the same thing):
```kt

//menus

  /* Standard Menu */
	val converter: Menu by lazy {
		menu(9 * 5) {
			title = "Exchange"

			frameWith(button(createItem(Material.GRAY_STAINED_GLASS_PANE) {
				displayName = " "
				lore = listOf("")
				flags(*ItemFlag.values())
			}) {
				protect = true
			})

			done(false)

			button(createItem(Config.SPIRIT_ITEM.type) {
				displayName = "Recycle"
			}) {
				click = { event, button ->
					val player = event.whoClicked
					if (player is Player)
						recycle(player).open(event.whoClicked)
				}
			} into slotrow2raw(4, 3)

			button(createItem(Material.CREEPER_HEAD) {
				displayName = "Acquire"
			}) {
				click = { event, button ->
					val player = event.whoClicked
					if (player is Player)
						acquire(player).open(event.whoClicked)
				}
			} into slotrow2raw(6, 3)

		}.also { it.generate() }
	}
  
  /* List Menu */
  private fun recycle(player: Player): MenuList<Pet> =
		menuListOf(9 * 5, FuseAddon.singletonCringe().mcPets.let { api ->
			api.available(player).mapNotNull { api.spriteFor(it)?.asOwned(player) }.map { it.pet() }
		}, transformer = { it.icon }) {
			title = "Recycle Fusemon"

			frameWith(button(createItem(Material.GRAY_STAINED_GLASS_PANE) {
				displayName = " "
				lore = listOf("")
				flags(*ItemFlag.values())
			}) {
				protect = true
			})

			//configure buttons
			back_next()
			done()

			fill = exclude(*frame())

			select = { event, _, pet ->
				if (pet.isCheckPermission) {
					val item = Config.SPIRIT_ITEM

					ConfPlaceholder.placeholder(item, pet.mythicMobName, Pets.PETS)
					FuseAddon.singletonCringe().mcPets.take(pet, event.whoClicked)
					Sprites.updatePlayer(event.whoClicked)

					event.whoClicked.inventory.addItem(item)

					items -= pet
					if (items.isEmpty()) event.whoClicked.closeInventory()
					else regen()
				}
			}

		}.also { it.generate() }

//helper methods

	fun MenuBuilder.frameWith(button: Button) {
		for (f in frame())
			button into f
	}

	fun MenuBuilder.frame(): IntArray {
		var frame = emptyArray<Int>()

		for (i in 0 until size.slots) {
			if (i < 9) frame += i//first row
			if ((i + 1) % 9 == 0 || i % 9 == 0) frame += i//sides (i or next multiple of 9)
			if (i >= size.slots - 9) frame += i//last row
		}

		return frame.toIntArray()
	}

	private fun MenuBuilder.done(setParent: Boolean = true) {
		if (setParent) parent = converter

		val item = if (setParent) {
			createItem(Material.ARROW) {
				displayName = "&9Go Back"
			}
		} else {
			createItem(Material.REDSTONE_TORCH) {
				displayName = "&4Exit"
			}
		}

		button(item) {
			protect = true
			click = { event, _ ->
				if (parent == null) event.whoClicked.closeInventory()
			}
		} into DONE_SLOT

		previousMenuButton = buttons[DONE_SLOT]!! to DONE_SLOT
	}

	fun MenuListBuilder<*>.back_next() {
		back = button(createItem(Material.TORCH) {
			displayName = "&3&l\u219c&r &3Previous Page"
		}) {
			protect = true
		} to BACK_SLOT

		next = button(createItem(Material.SOUL_TORCH) {
			displayName = "&3Next Page &l\u219d"
		}) {
			protect = true
		} to NEXT_SLOT
	}
```

This yields the following behavior:

![the above code in action](https://i.imgur.com/TRTvMZa.gif "DSL Menu Demo")


You can also use a template if you want multiple menus with the same layout. This only applies to `MenuList`s:

```kt
		val template = menu(9*5) {
			//each page, and child menu will have the same button layout
		}
		
		val list = template.toList(items) {
			//menu list builder here
		}
```
