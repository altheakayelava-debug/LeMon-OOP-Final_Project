/** Child of Item: makes a LeMon hit harder until the battle ends. */
public class BattleItem extends Item {
    private final double multiplier;

    public BattleItem(String name, double multiplier) {
        super(name, "Attack x" + multiplier + " for this battle");
        this.multiplier = multiplier;
    }

    @Override
    public String use(LeMon target) {
        target.setAttackBoost(multiplier);
        return target.getName() + "'s attack rose!";
    }
}
