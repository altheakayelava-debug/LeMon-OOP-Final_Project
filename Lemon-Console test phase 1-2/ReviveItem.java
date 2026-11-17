/** Child of Item: brings a FAINTED LeMon back. It can NOT be used on a LeMon that is still up. */
public class ReviveItem extends Item {
    private final double fraction;           // 0.5 = comes back with half of its max HP

    public ReviveItem(String name, double fraction) {
        super(name, "Revives a fainted LeMon with " + (int) (fraction * 100) + "% HP");
        this.fraction = fraction;
    }

    @Override
    public boolean canUseOn(LeMon target) {
        return target.isFainted();
    }

    @Override
    public String use(LeMon target) {
        int amount = Math.max(1, (int) (target.getMaxHp() * fraction));
        int restored = target.heal(amount);
        return target.getName() + " was revived with " + restored + " HP!";
    }
}
