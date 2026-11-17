/**
 * The "element" of a LeMon or a Move.
 * TO MODIFY: add a name to the list, then add a winning pair in multiplierAgainst().
 */
public enum Type {
    HARDWARE, SOFTWARE, PAPER, NORMAL;

    /** How strong an attack of THIS type is against a defender of another type. */
    public double multiplierAgainst(Type defender) {
        if (this == NORMAL || defender == NORMAL || this == defender) {
            return 1.0;                       // neutral
        }
        // Rock-paper-scissors: HARDWARE > SOFTWARE > PAPER > HARDWARE
        boolean strong = (this == HARDWARE && defender == SOFTWARE)
                      || (this == SOFTWARE && defender == PAPER)
                      || (this == PAPER    && defender == HARDWARE);
        return strong ? 2.0 : 0.5;            // super effective : not very effective
    }
}
