/** Child of Item: opens a locked door. It cannot be used on a LeMon. */
public class KeyItem extends Item {
    private final String doorId;     // which door this key opens

    public KeyItem(String name, String description, String doorId) {
        super(name, description);
        this.doorId = doorId;
    }

    @Override
    public boolean canUseOn(LeMon target) {
        return false;
    }

    @Override
    public String use(LeMon target) {
        return "The " + getName() + " can't be used on a LeMon.";
    }

    public String getDoorId() { return doorId; }
}
