/** A child of Move: heals the user instead of hurting the target. */
public class HealMove extends Move {
    public HealMove(String name, int healAmount) {
        super(name, Type.NORMAL, healAmount);
    }

    @Override
    public String use(LeMon user, LeMon target) {
        int healed = user.heal(getPower());
        return user.getName() + " used " + getName() + "! Healed " + healed + " HP";
    }
}
