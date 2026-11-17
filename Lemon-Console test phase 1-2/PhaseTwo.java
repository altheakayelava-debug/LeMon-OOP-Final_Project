import java.util.ArrayList;

/**
 * PHASE 2 of LeMon: Labyrinth Explorers Monsters.
 *
 * The first floor of the labyrinth, from the Phase 2 document.
 * Susie enters with her partner LeMon, Mya, survives a tutorial battle,
 * finds a stone gate that needs a crown-shaped key, goes down the left
 * path, defeats the floor guardian, takes its crown, opens the gate,
 * and meets the crazy man below.
 *
 * Every rule (damage, items, checkpoints, type chart) comes from the
 * classes that were already in the project. This class only tells the
 * story and asks the player what to do.
 */
public class PhaseTwo {

    /** Play the first floor from the entrance to the Phase 3 hook. */
    public static void play() {
        Player susie = new Player("Susie");

        titleScreen();
        outside(susie);
        firstCorridor(susie);
        theGate(susie);
        leftPath(susie);
        guardian(susie);
        crownGate(susie);
        crazyMan(susie);
        ending();

        Console.pause();
    }

    // ---------- the beginning ----------
    private static void titleScreen() {
        Console.banner(
                "LeMon: Labyrinth Explorers Monsters",
                "",
                "Phase 2 - The First Floor",
                "A console adventure");
    }

    private static void outside(Player susie) {
        Console.scene("Outside the Labyrinth");

        LeMon mya = new LeMon("Mya", Type.HARDWARE, 40, 13);
        mya.addMove(new Move("Mya Slam", Type.HARDWARE, 9));
        mya.addMove(new HealMove("Recover", 12));
        susie.addLeMon(mya);
        susie.move("Outside the Labyrinth");
        Console.message("Mya joined the party! (HARDWARE, 40 HP)");

        Console.narrate(Console.p(
                "Susie stands outside the labyrinth, admiring its beauty. It isn't tall, "
                        + "but it is huge.",
                "Her partner LeMon, Mya, waits beside her."));
        Console.say("SUSIE", "happy",
                "So that's how I got myself into this trouble, partner.");
        Console.say("MYA", "Mya.");
        Console.say("SUSIE", "happy",
                "This labyrinth is H U G E. Like, REALLY HUGE.");
        Console.say("SUSIE", "scared",
                "I'm scared though. People died here, and I'm here alone with my LeMon.");
        Console.say("MYA", "MYAAAA!");
        Console.say("SUSIE", "happy",
                "You're right, partner. I have you, so I don't need to be scared of these "
                        + "labyrinths. I know I could always count on you!");
        Console.say("MYA", "MYA MYA MYAAAA!!");
        Console.say("SUSIE", "scared", "O K A Y !");
        Console.say("SUSIE", "happy", "Let's go, partner. Let's explore this labyrinth!");

        Console.action("Susie enters the labyrinth. She walks until a monster drops from "
                + "the ceiling and blocks her path.");

        Console.message(susie.reachCheckpoint("Labyrinth Entrance"));
        Console.pause();
    }

    // ---------- the tutorial battle ----------
    private static void firstCorridor(Player susie) {
        Console.scene("Labyrinth Entrance - First Corridor");

        Console.narrate(Console.p(
                "This is a LeMon battle. Choose Fight to attack, or Bag to use an item.",
                "Types matter: HARDWARE beats SOFTWARE, SOFTWARE beats PAPER, and PAPER "
                        + "beats HARDWARE. NORMAL is neutral against everything."));
        Console.message("Susie checks her pack. There is one Coffee inside.");
        susie.addItem(new HealingItem("Coffee", 20));

        boolean won = false;
        while (!won) {
            LeMon imp = new LeMon("Glitch Imp", Type.NORMAL, 18, 8);
            imp.addMove(new Move("Bite", Type.NORMAL, 5));

            Battle battle = new Battle(susie, imp, new HealingItem("Coffee", 20));
            won = runBattle(susie, battle);

            if (!won) {
                Console.message("Susie wakes up at " + susie.getLocation() + ". Mya is okay.");
                Console.pause();
            }
        }

        Console.say("SUSIE", "happy", "Seems like we still have it, eh partner?");
        Console.say("MYA", "MYA MYA!");
        Console.pause();
    }

    // ---------- the gate ----------
    private static void theGate(Player susie) {
        Console.scene("The Labyrinth - The Fork");
        Console.narrate(Console.p(
                "As Susie goes deeper, she notices a locked door and a blood trail that "
                        + "leads past it. To her left, another path goes deeper into the floor."));

        Console.say("SUSIE", "scared", "A gate, huh? And a BLOOD TRAIL?!");
        Console.say("SUSIE", "scared", "So this is where those explorers died?");
        Console.say("SUSIE", "poker",
                "No, it can't be. It's only blood. They must be deeper... or at least some "
                        + "of them are deeper. Are they alive?");
        Console.say("SUSIE", "poker",
                "No, that can't be possible. The monster could have dragged them deeper. "
                        + "And how did those monsters open this gate anyway?");
        Console.say("SUSIE", "poker",
                "Also, where are the bodies Hans mentioned?");
        Console.say("SUSIE", "poker",
                "Did they become monsters here inside the labyrinth? No. The cartographers "
                        + "saw them. They must have already buried them outside.");

        Console.action("A rustle startles her.");
        Console.say("SUSIE", "scared", "AHHHHH!");
        Console.say("SUSIE", "poker", "Whew. It's just my partner.");
        Console.say("MYA", "Myaa...");
        Console.say("SUSIE", "happy",
                "It's okay, partner. I should stop overthinking. Let's examine this gate.");

        Console.action("Susie examines the gate.");
        Console.narrate("The gate is made of stone. There is no keyhole - only a peculiar "
                + "space that resembles a crown.");
        Console.say("SUSIE", "happy",
                "I see. All I need to do is find a crown-looking thing and put it here? "
                        + "That's simple enough, I think.");
        Console.say("SUSIE", "poker", "Maybe I should keep going deeper in this floor.");

        boolean leaving = false;
        while (!leaving) {
            int pick = Console.choose("What does Susie do?",
                    "Head down the left path",
                    "Try to force the gate open");

            if (pick == 1) {
                leaving = true;
            } else {
                Console.action("Susie pushes against the gate. It does not move.");
                Console.say("SUSIE", "poker", "Yeah. I need that crown thing.");
            }
        }

        Console.pause();
    }

    // ---------- the left path: three rooms ----------
    private static void leftPath(Player susie) {
        Console.scene("The Left Path - Bloodied Hall");
        Console.narrate("The blood trail runs along the floor here, thin and old. "
                + "Whatever made it did not stop walking.");
        int pick = Console.choose("What does Susie do?",
                "Search the room",
                "Keep moving");
        if (pick == 1) {
            Console.action("Susie checks behind a fallen stone.");
            susie.addItem(new HealingItem("Coffee", 20));
            Console.message("Susie found a Coffee (Restores 20 HP).");
        }

        Console.scene("The Left Path - Echoing Passage");
        Console.narrate("Every step comes back twice. The walls are covered in scratches, "
                + "like something counted the days down here.");
        pick = Console.choose("What does Susie do?",
                "Search the room",
                "Keep moving");
        if (pick == 1) {
            Console.narrate("Susie finds only dust and old claw marks. Nothing useful.");
        }

        Console.scene("The Left Path - Guardian's Antechamber");
        Console.narrate(Console.p(
                "The passage ends at a round stone door carved with a crown.",
                "Something heavy breathes on the other side."));
        Console.message(susie.reachCheckpoint("Guardian's Antechamber"));
        Console.say("SUSIE", "scared", "Okay. Crown first, then gate. I can do this.");
        Console.pause();
    }

    // ---------- the guardian ----------
    private static void guardian(Player susie) {
        boolean won = false;

        while (!won) {
            Console.scene("The Guardian's Chamber");
            Console.narrate(Console.p(
                    "The chamber opens into a perfect circle. In the center stands a massive "
                            + "stone guardian. A crown is embedded in its chest.",
                    "Above it, a long stalactite hangs from the ceiling."));

            boolean resolved = false;
            while (!resolved) {
                int pick = Console.choose("What does Susie do?",
                        "Study the guardian",
                        "Walk up and fight it",
                        "Throw a stone at the stalactite");

                if (pick == 1) {
                    Console.narrate("The guardian is built like a wall. Its arms are longer "
                            + "than Susie is tall. The crown sits in a crack in its chest.");
                    Console.say("SUSIE", "scared",
                            "Is that the guardian of this floor? It has the crown thingie "
                                    + "that goes in the gate. I must defeat it, but it looks strong.");
                } else if (pick == 2) {
                    Console.say("SUSIE", "scared",
                            "The guardian looks strong. I can't take it head-on. But maybe... "
                                    + "that stalactite above it could injure it.");
                } else {
                    Console.action("Susie picks up a loose stone and throws it at the ceiling.");
                    Console.narrate("The stalactite cracks, groans, and falls. The guardian "
                            + "takes the hit, and the whole chamber shakes.");
                    Console.say("SUSIE", "shocked", "It worked! Now, partner!");

                    LeMon guardian = new LeMon("Crowned Guardian", Type.SOFTWARE, 55, 12);
                    guardian.addMove(new Move("Crown Bash", Type.SOFTWARE, 10));
                    guardian.addMove(new Move("Stone Grip", Type.NORMAL, 14));

                    Battle battle = new Battle(susie, guardian,
                            new KeyItem("Crown", "Fits the crown-shaped slot in the stone gate", "crown"));
                    won = runBattle(susie, battle);
                    resolved = true;

                    if (!won) {
                        Console.say("SUSIE", "nervous",
                                "I'm really good at simulating fights in my head... and that "
                                        + "could happen if I'm not careful.");
                        Console.message("Susie wakes up at " + susie.getLocation() + ".");
                        Console.narrate("The guardian waits in the dark, exactly where it was.");
                        Console.pause();
                    }
                }
            }
        }

        Console.say("SUSIE", "tired", "Huff... puff... haahhhhhh!");
        Console.say("SUSIE", "tired", "Tough fight, partner!");
        Console.say("MYA", "MYA MYA MYA!");
        Console.action("Susie takes the crown from the fallen guardian.");
        Console.narrate("The guardian does not move again.");
        Console.pause();
    }

    // ---------- back to the gate ----------
    private static void crownGate(Player susie) {
        Console.scene("The Labyrinth - The Fork");
        Console.say("SUSIE", "poker", "I shall go back to the gate.");
        Console.narrate("Susie returns to the stone gate. The crown fits the slot perfectly.");

        Console.say("SUSIE", "happy", "I can finally go down in this thing.");
        Console.say("SUSIE", "scared",
                "Though I'm scared. There is blood, and that guardian was scary too.");
        Console.say("SUSIE", "scared",
                "What more abomination will I discover in this labyrinth?");

        Console.action("Susie places the crown in the slot. The gate grinds open, and a "
                + "stairway sinks into the dark.");
        Console.pause();
    }

    // ---------- the crazy man ----------
    private static void crazyMan(Player susie) {
        Console.scene("Below the Gate - The Stairwell");
        Console.narrate("At the bottom of the stairs, a man sits against the wall. "
                + "He is laughing to himself.");

        boolean talked = false;
        boolean grabbed = false;
        while (!talked) {
            int pick = Console.choose("What does Susie do?",
                    "Talk to the man",
                    "Walk past him");

            if (pick == 1) {
                talked = true;
            } else if (!grabbed) {
                grabbed = true;
                Console.action("Susie tries to walk past. The man grabs her sleeve.");
                Console.say("CRAZY MAN", "LISTEN TO ME, GIRL!");
            } else {
                Console.action("The man does not let go. He is stronger than he looks.");
            }
        }

        Console.say("CRAZY MAN", "AT THE CROSSROAD, DON'T TURN LEFT!");
        Console.say("CRAZY MAN", "AT THE CROSSROAD, DON'T TURN LEFT!");
        Console.say("CRAZY MAN", "LISTEN TO ME, GIRL. DO NOT TURN LEFT AT THE CROSSROAD!");
        Console.say("CRAZY MAN",
                "NO MATTER WHAT YOU DO, DON'T TURN LEFT. IF YOU DO, YOU STILL HAVE A "
                        + "CHANCE TO GO RIGHT!");
        Console.say("CRAZY MAN",
                "DO NOT TURN LEFT AT EVERY CROSSROAD! THI- THIS LABYRINTH IS FAKE. "
                        + "ITS- ALL OF THIS IS AN ILLUSION OF THE KING!");
        Console.say("CRAZY MAN", "QB ABG TB YRSG");
        Console.say("CRAZY MAN", "V'YY OR JNVGVAT SBE LBH ORUVAQ GUR TNGRF.");
        Console.say("CRAZY MAN", "KEH HEH HEH HEH HEH HEH HEH HAHAHAHAH!");

        Console.say("SUSIE", "scared", "What's up with this man? He's crazy.");
        Console.say("SUSIE", "scared", "I should probably go down now... and I'll be careful.");
        Console.narrate("Susie takes one last look at the laughing man, then walks deeper "
                + "into the dark.");
    }

    private static void ending() {
        Console.banner(
                "PHASE 2 COMPLETE",
                "\"The First Floor\"",
                "",
                "Next: Phase 3 - The Crossroads");
    }

    // ---------- the battle screen ----------
    /** Play one battle to the end. Returns true when Susie wins. */
    private static boolean runBattle(Player susie, Battle battle) {
        printAll(battle.startBattle());

        while (!battle.isOver()) {
            if (battle.needsSwitch()) {
                Console.message("Susie must choose another LeMon!");
                int pick = choosePartyMember(susie, "Who steps up?");
                if (pick < 0) {
                    break;
                }
                printAll(battle.switchLeMon(pick));
                continue;
            }

            showBattle(battle);
            int choice = Console.choose("What will Susie do?", "Fight", "Bag");

            ArrayList<String> log = null;
            if (choice == 1) {
                int move = chooseMove(battle.getPlayerLeMon());
                if (move >= 0) {
                    log = battle.useMove(move);
                }
            } else {
                log = useBagInBattle(susie, battle);
            }

            if (log != null) {
                printAll(log);
            }
        }

        return battle.getState() == Battle.State.WON;
    }

    private static void showBattle(Battle battle) {
        LeMon mine = battle.getPlayerLeMon();
        LeMon wild = battle.getOpponentLeMon();
        Console.rule('-');
        Console.write("BATTLE - turn " + battle.getTurn());
        Console.write("YOU   " + mine.getName() + " (" + mine.getType() + ")  " + hpBar(mine));
        Console.write("WILD  " + wild.getName() + " (" + wild.getType() + ")  " + hpBar(wild));
        Console.rule('-');
    }

    private static int chooseMove(LeMon lemon) {
        ArrayList<Move> moves = lemon.getMoves();
        if (moves.isEmpty()) {
            Console.message(lemon.getName() + " has no moves!");
            return -1;
        }

        ArrayList<String> labels = new ArrayList<>();
        for (Move move : moves) {
            if (move instanceof HealMove) {
                labels.add(move.getName() + " (" + move.getType() + ", heals " + move.getPower() + ")");
            } else {
                labels.add(move.getName() + " (" + move.getType() + ", power " + move.getPower() + ")");
            }
        }
        labels.add("Back to Fight / Bag");

        int pick = Console.choose("Which move?", labels.toArray(new String[0]));
        if (pick == labels.size()) {
            return -1;                       // changed their mind: no turn is used
        }
        return pick - 1;
    }

    private static ArrayList<String> useBagInBattle(Player susie, Battle battle) {
        ArrayList<Item> bag = susie.getBag();
        if (bag.isEmpty()) {
            Console.message("The bag is empty.");
            return null;
        }

        ArrayList<String> labels = new ArrayList<>();
        for (Item item : bag) {
            labels.add(item.getName() + " - " + item.getDescription());
        }
        labels.add("Back to Fight / Bag");

        int pick = Console.choose("Use which item?", labels.toArray(new String[0]));
        if (pick == labels.size()) {
            return null;                     // changed their mind: no turn is used
        }
        return battle.useItem(pick - 1);
    }

    private static int choosePartyMember(Player susie, String question) {
        ArrayList<LeMon> party = susie.getParty();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> slots = new ArrayList<>();

        for (int i = 0; i < party.size(); i++) {
            LeMon lemon = party.get(i);
            if (lemon.isFainted()) {
                continue;
            }
            labels.add(lemon.getName() + " (" + lemon.getType() + ")  " + hpBar(lemon));
            slots.add(i);
        }

        if (labels.isEmpty()) {
            return -1;
        }
        int pick = Console.choose(question, labels.toArray(new String[0])) - 1;
        return slots.get(pick);
    }

    // ---------- small helpers ----------
    private static void printAll(ArrayList<String> log) {
        for (String line : log) {
            Console.message(line);
        }
    }

    private static String hpBar(LeMon lemon) {
        int width = 10;
        int filled = 0;
        if (lemon.getMaxHp() > 0) {
            filled = (int) Math.round(width * (double) lemon.getHp() / lemon.getMaxHp());
        }
        String bar = "";
        for (int i = 0; i < width; i++) {
            bar += (i < filled) ? "#" : ".";
        }
        return "[" + bar + "] " + lemon.getHp() + "/" + lemon.getMaxHp();
    }
}
