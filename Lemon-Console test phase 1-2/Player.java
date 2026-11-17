import java.util.ArrayList;

/** The user's character (Susie): her LeMon, her bag, and where she is. */
public class Player {
    public static final int PARTY_LIMIT = 2;

    private final String name;
    private final ArrayList<LeMon> party = new ArrayList<>();
    private final ArrayList<LeMon> storage = new ArrayList<>();
    private final ArrayList<Item> bag = new ArrayList<>();
    private String location = "Start";
    private Checkpoint lastCheckpoint = null;       // null until the first checkpoint

    public Player(String name) {
        this.name = name;
    }

    // ---------- party and storage ----------
    /** Returns true if the LeMon joined the party, false if the party was full (it goes to storage). */
    public boolean addLeMon(LeMon lemon) {
        if (party.size() < PARTY_LIMIT) {
            party.add(lemon);
            return true;
        }
        storage.add(lemon);
        return false;
    }

    public ArrayList<LeMon> getParty() {
        return new ArrayList<>(party);
    }

    public ArrayList<LeMon> getStorage() {
        return new ArrayList<>(storage);
    }

    /** Swap a party LeMon with a stored one. Returns false if a number is not valid. */
    public boolean swapWithStorage(int partyIndex, int storageIndex) {
        if (partyIndex < 0 || partyIndex >= party.size()) {
            return false;
        }
        if (storageIndex < 0 || storageIndex >= storage.size()) {
            return false;
        }
        LeMon fromParty = party.get(partyIndex);
        LeMon fromStorage = storage.get(storageIndex);
        party.set(partyIndex, fromStorage);
        storage.set(storageIndex, fromParty);
        return true;
    }

    // ---------- who fights first ----------
    /** The first LeMon in the party that has not fainted, or null if they all fainted. */
    public LeMon getFirstHealthyLeMon() {
        for (LeMon lemon : party) {
            if (!lemon.isFainted()) {
                return lemon;
            }
        }
        return null;
    }

    public boolean hasHealthyLeMon() {
        return getFirstHealthyLeMon() != null;
    }

    // ---------- the bag ----------
    public void addItem(Item item) {
        bag.add(item);
    }

    public ArrayList<Item> getBag() {
        return new ArrayList<>(bag);
    }

    /**
     * Use the item at 'index' on 'target'. If it works, the item is removed from the bag.
     * Throws InvalidMoveException (and keeps the item) if the number is wrong or the item can't be used.
     */
    public String useItem(int index, LeMon target) throws InvalidMoveException {
        if (index < 0 || index >= bag.size()) {
            throw new InvalidMoveException("That item does not exist: " + (index + 1));
        }
        Item item = bag.get(index);
        if (!item.canUseOn(target)) {
            throw new InvalidMoveException(item.getName() + " can't be used on " + target.getName() + ".");
        }
        String message = item.use(target);
        bag.remove(index);
        return message;
    }

    /** Fully heals (and revives) every LeMon in the party and in storage. */
    public void healAll() {
        for (LeMon lemon : party) {
            lemon.heal(lemon.getMaxHp());
        }
        for (LeMon lemon : storage) {
            lemon.heal(lemon.getMaxHp());
        }
    }

    /** Every LeMon loses any attack boost (called when a battle ends). */
    public void resetAllBoosts() {
        for (LeMon lemon : party) {
            lemon.resetBoost();
        }
        for (LeMon lemon : storage) {
            lemon.resetBoost();
        }
    }

    // ---------- viewing and moving ----------
    public String viewLeMon() {
        if (party.isEmpty()) {
            return "You have no LeMon.";
        }
        String text = "";
        for (int i = 0; i < party.size(); i++) {
            LeMon lemon = party.get(i);
            text += (i + 1) + ". " + lemon.getName() + " (" + lemon.getType() + ") HP "
                    + lemon.getHp() + "/" + lemon.getMaxHp();
            if (lemon.isFainted()) {
                text += " [fainted]";
            }
            if (i < party.size() - 1) {
                text += "\n";
            }
        }
        return text;
    }

    public String viewBag() {
        if (bag.isEmpty()) {
            return "Your bag is empty.";
        }
        String text = "";
        for (int i = 0; i < bag.size(); i++) {
            text += (i + 1) + ". " + bag.get(i).getName() + " - " + bag.get(i).getDescription();
            if (i < bag.size() - 1) {
                text += "\n";
            }
        }
        return text;
    }

    public void move(String newLocation) {
        location = newLocation;
    }

    // ---------- checkpoints ----------
    /** Remember the game right now (location, LeMon, bag). Replaces the older checkpoint. */
    public String saveCheckpoint() {
        lastCheckpoint = new Checkpoint(location, party, storage, bag);
        return "Checkpoint saved at " + location + ".";
    }

    /** Arrive at a safe place: your LeMon are healed and the game is saved. */
    public String reachCheckpoint(String safePlace) {
        move(safePlace);
        healAll();
        return saveCheckpoint();
    }

    /** Go back to the last checkpoint (this is what happens when you lose a battle). */
    public String returnToCheckpoint() {
        if (lastCheckpoint == null) {
            healAll();
            return "No checkpoint yet. Your LeMon were healed where you fell.";
        }
        party.clear();
        party.addAll(lastCheckpoint.getParty());
        storage.clear();
        storage.addAll(lastCheckpoint.getStorage());
        bag.clear();
        bag.addAll(lastCheckpoint.getBag());
        location = lastCheckpoint.getLocation();
        if (!hasHealthyLeMon()) {
            healAll();                          // safety: never come back with nobody able to fight
        }
        return "You returned to your last checkpoint: " + location + ".";
    }

    public boolean hasCheckpoint() { return lastCheckpoint != null; }

    public String getLastCheckpointLocation() {
        return lastCheckpoint == null ? null : lastCheckpoint.getLocation();
    }

    public String getLocation() { return location; }
    public String getName()     { return name; }
}
