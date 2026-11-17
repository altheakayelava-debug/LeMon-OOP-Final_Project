/** An attack card. A normal Move deals damage to the target. */
public class Move {
    private final String name;
    private final Type type;
    private final int power;

    public Move(String name, Type type, int power) {
        this.name = name;
        this.type = type;
        this.power = power;
    }

    public String getName() { return name; }
    public Type getType()   { return type; }
    public int getPower()   { return power; }

    /** What happens when this move is used. Returns a message to show the player. */
    public String use(LeMon user, LeMon target) {
        double multiplier = type.multiplierAgainst(target.getType());
        int damage = (int) (power * multiplier * user.getAttackBoost());
        if (damage < 1) {
            damage = 1;
        }
        target.takeDamage(damage);
        return user.getName() + " used " + name + "! " + damage + " damage";
    }
}
