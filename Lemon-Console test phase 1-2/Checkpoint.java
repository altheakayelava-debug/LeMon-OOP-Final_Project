import java.util.ArrayList;

/**
 * A saved moment of the game: where you were, your party and storage (with their HP),
 * and what was in your bag. It only holds COPIES, so later changes can't touch the save.
 */
public class Checkpoint {
    private final String location;
    private final ArrayList<LeMon> party = new ArrayList<>();
    private final ArrayList<LeMon> storage = new ArrayList<>();
    private final ArrayList<Item> bag = new ArrayList<>();

    public Checkpoint(String location, ArrayList<LeMon> party, ArrayList<LeMon> storage, ArrayList<Item> bag) {
        this.location = location;
        for (LeMon lemon : party) {
            this.party.add(lemon.copy());
        }
        for (LeMon lemon : storage) {
            this.storage.add(lemon.copy());
        }
        this.bag.addAll(bag);                 // items never change, so the same items are fine
    }

    public String getLocation() { return location; }

    /** Fresh copies every time, so loading the same checkpoint twice works. */
    public ArrayList<LeMon> getParty() {
        ArrayList<LeMon> copies = new ArrayList<>();
        for (LeMon lemon : party) {
            copies.add(lemon.copy());
        }
        return copies;
    }

    public ArrayList<LeMon> getStorage() {
        ArrayList<LeMon> copies = new ArrayList<>();
        for (LeMon lemon : storage) {
            copies.add(lemon.copy());
        }
        return copies;
    }

    public ArrayList<Item> getBag() {
        return new ArrayList<>(bag);
    }
}
