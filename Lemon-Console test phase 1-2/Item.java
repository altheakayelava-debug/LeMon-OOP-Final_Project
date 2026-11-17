/**
 * The PARENT of every item. "abstract" means you can't write new Item(...);
 * you must pick a child: HealingItem, BattleItem, or KeyItem.
 * Every child must say what use() does (that is polymorphism).
 */
public abstract class Item {
    private final String name;
    private final String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** Use the item on a LeMon. Returns a message to show the player. */
    public abstract String use(LeMon target);

    /** Can this item be used on this LeMon right now? Children can change this rule. */
    public boolean canUseOn(LeMon target) {
        return !target.isFainted();
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
}
