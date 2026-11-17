import java.util.ArrayList;
import java.util.Random;

/**
 * Runs ONE fight. It never prints anything: every action returns a list of messages,
 * and the screen (console now, window later) decides how to show them.
 *
 * A turn: you choose (fight / bag / switch) -> the enemy answers -> checkWinner().
 * A wrong choice (bad number, item can't be used...) is caught here and costs NO turn.
 */
public class Battle {
    public enum State { ONGOING, WON, LOST }

    private final Player player;
    private LeMon playerLeMon;
    private final LeMon opponentLeMon;
    private final Item reward;               // item you get for winning (can be null)
    private final Random random = new Random();
    private int turn = 1;
    private State state = State.ONGOING;
    private boolean mustSwitch = false;     // true while you MUST pick a new LeMon

    public Battle(Player player, LeMon opponentLeMon) {
        this(player, opponentLeMon, null);
    }

    public Battle(Player player, LeMon opponentLeMon, Item reward) {
        this.player = player;
        this.opponentLeMon = opponentLeMon;
        this.reward = reward;
        this.playerLeMon = player.getFirstHealthyLeMon();
    }

    // ---------- start ----------
    public ArrayList<String> startBattle() {
        ArrayList<String> log = new ArrayList<>();
        if (playerLeMon == null) {
            state = State.LOST;
            log.add("You have no LeMon able to fight!");
            log.add(player.returnToCheckpoint());
            return log;
        }
        log.add("A battle begins! " + opponentLeMon.getName() + " appeared!");
        log.add("Go, " + playerLeMon.getName() + "!");
        return log;
    }

    // ---------- who won? ----------
    public ArrayList<String> checkWinner() {
        ArrayList<String> log = new ArrayList<>();
        if (isOver()) {
            return log;
        }
        if (opponentLeMon.isFainted()) {
            state = State.WON;
            log.add(opponentLeMon.getName() + " fainted! You won!");
            if (reward != null) {
                player.addItem(reward);
                log.add("You got " + reward.getName() + "!");
            }
            player.resetAllBoosts();
        } else if (playerLeMon.isFainted() && !mustSwitch) {
            log.add(playerLeMon.getName() + " fainted!");
            if (player.hasHealthyLeMon()) {
                mustSwitch = true;                        // YOU must choose the next LeMon
                log.add("Choose another LeMon!");
            } else {
                state = State.LOST;
                log.add("You have no LeMon left... you lost!");
                player.resetAllBoosts();
                log.add(player.returnToCheckpoint());        // all LeMon wiped out: back to the last checkpoint
            }
        }
        return log;
    }

    // ---------- the enemy's turn ----------
    public ArrayList<String> executeTurn() {
        ArrayList<String> log = new ArrayList<>();
        if (isOver() || mustSwitch) {
            return log;
        }
        ArrayList<Move> moves = opponentLeMon.getMoves();
        if (moves.isEmpty()) {
            log.add(opponentLeMon.getName() + " has no moves!");
            return log;
        }
        Move move = moves.get(random.nextInt(moves.size()));
        log.add(opponentLeMon.attack(playerLeMon, move));
        log.addAll(checkWinner());
        return log;
    }

    // ---------- choice 1: FIGHT ----------
    public ArrayList<String> useMove(int moveIndex) {
        ArrayList<String> log = new ArrayList<>();
        if (isOver()) {
            return log;
        }
        if (mustSwitch) {
            log.add("You must choose another LeMon first!");
            return log;
        }
        try {
            Move move = playerLeMon.getMove(moveIndex);       // may throw InvalidMoveException
            LeMon attacker = playerLeMon;

            if (opponentLeMon.isFasterThan(playerLeMon)) {
                log.addAll(executeTurn());                    // the enemy is faster: it goes first
                if (!isOver() && !mustSwitch) {               // ...and we only move if still standing
                    log.add(attacker.attack(opponentLeMon, move));
                    log.addAll(checkWinner());
                }
            } else {
                log.add(attacker.attack(opponentLeMon, move)); // we are faster (or tied): we go first
                log.addAll(checkWinner());
                if (!isOver()) {
                    log.addAll(executeTurn());
                }
            }
            turn++;
        } catch (InvalidMoveException e) {
            log.add(e.getMessage());                          // wrong choice: no turn is used
        }
        return log;
    }

    // ---------- choice 2: BAG ----------
    /** Use an item on the LeMon that is fighting right now. */
    public ArrayList<String> useItem(int itemIndex) {
        return useItem(itemIndex, player.getParty().indexOf(playerLeMon));
    }

    /** Use an item on ANY LeMon in your party (for example, a Revive on a fainted one). */
    public ArrayList<String> useItem(int itemIndex, int partyIndex) {
        ArrayList<String> log = new ArrayList<>();
        if (isOver()) {
            return log;
        }
        if (mustSwitch) {
            log.add("You must choose another LeMon first!");
            return log;
        }
        try {
            ArrayList<LeMon> party = player.getParty();
            if (partyIndex < 0 || partyIndex >= party.size()) {
                throw new InvalidMoveException("No LeMon in that slot: " + (partyIndex + 1));
            }
            log.add(player.useItem(itemIndex, party.get(partyIndex)));   // items go first, then the enemy answers
            log.addAll(executeTurn());
            turn++;
        } catch (InvalidMoveException e) {
            log.add(e.getMessage());
        }
        return log;
    }

    // ---------- choice 3: SWITCH LEMON ----------
    public ArrayList<String> switchLeMon(int partyIndex) {
        ArrayList<String> log = new ArrayList<>();
        if (isOver()) {
            return log;
        }
        try {
            ArrayList<LeMon> party = player.getParty();
            if (partyIndex < 0 || partyIndex >= party.size()) {
                throw new InvalidMoveException("No LeMon in that slot: " + (partyIndex + 1));
            }
            LeMon chosen = party.get(partyIndex);
            if (chosen.isFainted()) {
                throw new InvalidMoveException(chosen.getName() + " has fainted and can't fight!");
            }
            if (chosen == playerLeMon) {
                throw new InvalidMoveException(chosen.getName() + " is already fighting!");
            }
            playerLeMon = chosen;
            log.add("Go, " + chosen.getName() + "!");
            if (mustSwitch) {
                mustSwitch = false;                   // a forced swap is FREE: the enemy does not attack
            } else {
                log.addAll(executeTurn());            // a normal swap costs your turn
                turn++;
            }
        } catch (InvalidMoveException e) {
            log.add(e.getMessage());
        }
        return log;
    }

    // ---------- reading the battle ----------
    public boolean isOver()         { return state != State.ONGOING; }
    public boolean needsSwitch()    { return mustSwitch; }
    public State getState()         { return state; }
    public int getTurn()            { return turn; }
    public LeMon getPlayerLeMon()   { return playerLeMon; }
    public LeMon getOpponentLeMon() { return opponentLeMon; }
}
