/** Child of Item: restores HP. */
public class HealingItem extends Item {
    private final int healAmount;

    public HealingItem(String name, int healAmount) {
        super(name, "Restores " + healAmount + " HP");
        this.healAmount = healAmount;
    }

    @Override
    public boolean canUseOn(LeMon target) {
        return !target.isFainted() && target.getHp() < target.getMaxHp();
    }

    @Override
    public String use(LeMon target) {
        int healed = target.heal(healAmount);
        return target.getName() + " recovered " + healed + " HP!";
    }
}
