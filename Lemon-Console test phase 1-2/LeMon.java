import java.util.ArrayList;

/** The monster: name, type, HP, speed, and up to 4 moves. */
public class LeMon {
    public static final int MAX_MOVES = 4;

    private final String name;
    private final Type type;
    private final int maxHp;
    private int hp;
    private final int speed;
    private double attackBoost = 1.0;     // 1.0 = normal, changed by a BattleItem
    private final ArrayList<Move> moves = new ArrayList<>();

    public LeMon(String name, Type type, int maxHp, int speed) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
    }

    /** An independent twin: same stats, same HP, same moves, same boost. */
    public LeMon copy() {
        LeMon twin = new LeMon(name, type, maxHp, speed);
        for (Move move : moves) {
            twin.addMove(move);
        }
        twin.takeDamage(maxHp - hp);
        twin.setAttackBoost(attackBoost);
        return twin;
    }

    // ---------- fighting ----------
    public String attack(LeMon target, Move move) {
        if (this.isFainted()) {
            return name + " has fainted and can't move!";
        }
        return move.use(this, target);
    }

    public boolean isFasterThan(LeMon other) {
        return this.speed > other.speed;
    }

    // ---------- moves ----------
    public boolean addMove(Move move) {
        if (moves.size() >= MAX_MOVES) {
            return false;
        }
        moves.add(move);
        return true;
    }

    public Move getMove(int index) throws InvalidMoveException {
        if (index < 0 || index >= moves.size()) {
            throw new InvalidMoveException("That move does not exist: " + (index + 1));
        }
        return moves.get(index);
    }

    public ArrayList<Move> getMoves() {
        return new ArrayList<>(moves);
    }

    // ---------- HP ----------
    public void takeDamage(int amount) {
        hp = hp - amount;
        if (hp < 0) {
            hp = 0;
        }
    }

    public int heal(int amount) {
        int before = hp;
        hp = hp + amount;
        if (hp > maxHp) {
            hp = maxHp;
        }
        return hp - before;
    }

    public boolean isFainted() {
        return hp == 0;
    }

    // ---------- battle boost ----------
    public void setAttackBoost(double boost) { attackBoost = boost; }
    public void resetBoost()                 { attackBoost = 1.0; }
    public double getAttackBoost()           { return attackBoost; }

    // ---------- getters ----------
    public String getName() { return name; }
    public Type getType()   { return type; }
    public int getHp()      { return hp; }
    public int getMaxHp()   { return maxHp; }
    public int getSpeed()   { return speed; }
}
