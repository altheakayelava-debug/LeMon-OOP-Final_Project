import java.util.ArrayList;

/**
 * Every test lives here. Each check prints PASS or FAIL.
 * If you change the code and a test says FAIL, you broke something.
 */
public class Tests {
    static int passed = 0;
    static int failed = 0;

    static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + testName);
        } else {
            failed++;
            System.out.println("FAIL  " + testName);
        }
    }

    public static void runAll() {
        testTypes();
        testMove();
        testLeMonBasics();
        testHp();
        testMoveList();
        testAttack();
        testHealMove();
        testItems();
        testReviveItem();
        testLeMonCopy();
        testCheckpoints();
        testPlayerParty();
        testPlayerStorage();
        testPlayerBag();
        testPlayerViewAndMove();
        testPlayerScenario();
        testBattleSetup();
        testBattleCheckWinner();
        testBattleFight();
        testBattleBag();
        testBattleSwitch();
        testBattleForcedSwap();
        testBattleItemTarget();
        testBattleRewards();
        testBattleLossCheckpoint();
        testBattleFullFight();
        testEverythingTogether();

        System.out.println();
        System.out.println("Result: " + passed + " passed, " + failed + " failed");
        if (failed == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("SOMETHING IS BROKEN - look at the FAIL lines above");
        }
    }

    // ---------- 1. Type rules ----------
    static void testTypes() {
        System.out.println("--- Types ---");
        check("HARDWARE beats SOFTWARE (2.0)", Type.HARDWARE.multiplierAgainst(Type.SOFTWARE) == 2.0);
        check("SOFTWARE beats PAPER (2.0)",    Type.SOFTWARE.multiplierAgainst(Type.PAPER) == 2.0);
        check("PAPER beats HARDWARE (2.0)",    Type.PAPER.multiplierAgainst(Type.HARDWARE) == 2.0);
        check("SOFTWARE vs HARDWARE is weak (0.5)", Type.SOFTWARE.multiplierAgainst(Type.HARDWARE) == 0.5);
        check("NORMAL is neutral (1.0)",       Type.NORMAL.multiplierAgainst(Type.PAPER) == 1.0);
        check("Same type is neutral (1.0)",    Type.HARDWARE.multiplierAgainst(Type.HARDWARE) == 1.0);
    }

    // ---------- 2. Move ----------
    static void testMove() {
        System.out.println("--- Move ---");
        Move slap = new Move("Typo Slap", Type.NORMAL, 6);
        check("Move name",  slap.getName().equals("Typo Slap"));
        check("Move type",  slap.getType() == Type.NORMAL);
        check("Move power", slap.getPower() == 6);
    }

    // ---------- 3. Creating a LeMon + speed ----------
    static void testLeMonBasics() {
        System.out.println("--- LeMon basics ---");
        LeMon pc  = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bug = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        check("Name",  pc.getName().equals("Compy"));
        check("Type",  pc.getType() == Type.HARDWARE);
        check("Starts at full HP", pc.getHp() == 40 && pc.getMaxHp() == 40);
        check("Speed", pc.getSpeed() == 12);
        check("Not fainted at start", !pc.isFainted());
        check("Bugsy (15) is faster than Compy (12)", bug.isFasterThan(pc));
        check("Compy is NOT faster than Bugsy", !pc.isFasterThan(bug));
    }

    // ---------- 4. HP: damage, heal, faint ----------
    static void testHp() {
        System.out.println("--- HP ---");
        LeMon pc = new LeMon("Compy", Type.HARDWARE, 40, 12);
        pc.takeDamage(15);
        check("takeDamage(15): 40 -> 25", pc.getHp() == 25);
        int healed = pc.heal(10);
        check("heal(10) returns 10", healed == 10);
        check("HP is now 35", pc.getHp() == 35);
        healed = pc.heal(20);
        check("heal(20) only heals 5 (cap at max)", healed == 5);
        check("HP never goes above max", pc.getHp() == 40);
        pc.takeDamage(100);
        check("HP never goes below 0", pc.getHp() == 0);
        check("isFainted() is true at 0 HP", pc.isFainted());
    }

    // ---------- 5. Move list + exception ----------
    static void testMoveList() {
        System.out.println("--- Move list ---");
        LeMon pc = new LeMon("Compy", Type.HARDWARE, 40, 12);
        check("1st move added", pc.addMove(new Move("A", Type.NORMAL, 1)));
        check("2nd move added", pc.addMove(new Move("B", Type.NORMAL, 2)));
        check("3rd move added", pc.addMove(new Move("C", Type.NORMAL, 3)));
        check("4th move added", pc.addMove(new Move("D", Type.NORMAL, 4)));
        check("5th move is REFUSED (limit 4)", !pc.addMove(new Move("E", Type.NORMAL, 5)));
        check("List has exactly 4 moves", pc.getMoves().size() == 4);

        try {
            check("getMove(0) is move A", pc.getMove(0).getName().equals("A"));
            check("getMove(3) is move D", pc.getMove(3).getName().equals("D"));
        } catch (InvalidMoveException e) {
            check("valid getMove should not throw", false);
        }

        boolean caughtHigh = false;
        try {
            pc.getMove(9);
        } catch (InvalidMoveException e) {
            caughtHigh = e.getMessage().equals("That move does not exist: 10");
        }
        check("getMove(9) throws InvalidMoveException", caughtHigh);

        boolean caughtNegative = false;
        try {
            pc.getMove(-1);
        } catch (InvalidMoveException e) {
            caughtNegative = true;
        }
        check("getMove(-1) throws InvalidMoveException", caughtNegative);

        ArrayList<Move> copy = pc.getMoves();
        copy.clear();
        check("Clearing the copy does NOT empty the real list", pc.getMoves().size() == 4);
    }

    // ---------- 6. Attacking ----------
    static void testAttack() {
        System.out.println("--- Attack ---");
        LeMon pc  = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bug = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        Move crash = new Move("System Crash", Type.HARDWARE, 9);
        Move compile = new Move("Compile Error", Type.SOFTWARE, 8);

        String msg = pc.attack(bug, crash);
        check("Message is correct", msg.equals("Compy used System Crash! 18 damage"));
        check("Super effective: 9 x 2.0 = 18 damage (35 -> 17)", bug.getHp() == 17);

        bug.attack(pc, compile);
        check("Not very effective: 8 x 0.5 = 4 damage (40 -> 36)", pc.getHp() == 36);

        LeMon tank = new LeMon("Tank", Type.HARDWARE, 40, 5);
        Move poke = new Move("Poke", Type.SOFTWARE, 1);
        poke.use(bug, tank);
        check("Damage is always at least 1", tank.getHp() == 39);

        bug.takeDamage(100);
        int hpBefore = pc.getHp();
        String fainted = bug.attack(pc, compile);
        check("Fainted LeMon cannot attack (message)", fainted.equals("Bugsy has fainted and can't move!"));
        check("Fainted LeMon cannot attack (no damage)", pc.getHp() == hpBefore);
    }

    // ---------- 7. HealMove (inheritance + polymorphism) ----------
    static void testHealMove() {
        System.out.println("--- HealMove ---");
        LeMon pc  = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bug = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        Move recover = new HealMove("Recover", 12);

        check("HealMove IS a Move (inheritance)", recover instanceof Move);
        check("HealMove type is NORMAL", recover.getType() == Type.NORMAL);

        pc.takeDamage(10);
        String msg = pc.attack(bug, recover);
        check("Heal message", msg.equals("Compy used Recover! Healed 10 HP"));
        check("User is healed back to 40", pc.getHp() == 40);
        check("The target is NOT hurt by a heal move", bug.getHp() == 35);

        msg = pc.attack(bug, recover);
        check("Healing at full HP heals 0", msg.equals("Compy used Recover! Healed 0 HP"));
    }

    // ---------- 8. Everything together: a mini battle ----------
    static void testItems() {
        System.out.println("--- Items ---");
        Item coffee = new HealingItem("Coffee", 20);
        Item drink  = new BattleItem("Energy Drink", 1.5);
        Item key    = new KeyItem("Rusty Key", "Opens the first locked door", "door1");

        // every kind of item IS an Item (inheritance)
        check("HealingItem is an Item", coffee instanceof Item);
        check("BattleItem is an Item",  drink instanceof Item);
        check("KeyItem is an Item",     key instanceof Item);
        check("Item name",        coffee.getName().equals("Coffee"));
        check("Healing description", coffee.getDescription().equals("Restores 20 HP"));
        check("Battle description",  drink.getDescription().equals("Attack x1.5 for this battle"));

        // --- HealingItem ---
        LeMon pc = new LeMon("Compy", Type.HARDWARE, 40, 12);
        check("Healing item is useless at full HP", !coffee.canUseOn(pc));
        pc.takeDamage(30);
        check("Healing item works on a hurt LeMon", coffee.canUseOn(pc));
        check("Heal message", coffee.use(pc).equals("Compy recovered 20 HP!"));
        check("HP went 10 -> 30", pc.getHp() == 30);
        pc.takeDamage(5);
        check("Healing is capped: only 15 HP restored", coffee.use(pc).equals("Compy recovered 15 HP!"));
        check("HP never goes above max", pc.getHp() == 40);
        pc.takeDamage(100);
        check("Healing item cannot be used on a fainted LeMon", !coffee.canUseOn(pc));

        // --- BattleItem ---
        LeMon hero   = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon target = new LeMon("Bugsy", Type.SOFTWARE, 100, 10);
        Move crash = new Move("System Crash", Type.HARDWARE, 9);
        hero.attack(target, crash);
        check("Normal hit: 18 damage", target.getHp() == 82);
        check("Battle item message", drink.use(hero).equals("Compy's attack rose!"));
        hero.attack(target, crash);
        check("Boosted hit: 9 x 2.0 x 1.5 = 27 damage", target.getHp() == 55);
        hero.resetBoost();
        hero.attack(target, crash);
        check("After resetBoost the hit is 18 again", target.getHp() == 37);

        // --- KeyItem ---
        check("Key item cannot be used on a LeMon", !key.canUseOn(hero));
        check("Key item message", key.use(hero).equals("The Rusty Key can't be used on a LeMon."));
        check("Key item knows its door", ((KeyItem) key).getDoorId().equals("door1"));

        // --- POLYMORPHISM: one list, one call, three different results ---
        ArrayList<Item> bag = new ArrayList<>();
        bag.add(coffee);
        bag.add(drink);
        bag.add(key);
        LeMon hurt = new LeMon("Compy", Type.HARDWARE, 40, 12);
        hurt.takeDamage(20);
        int used = 0;
        for (Item item : bag) {
            if (item.canUseOn(hurt)) {
                System.out.println(item.use(hurt));
                used++;
            } else {
                System.out.println("(cannot use " + item.getName() + ")");
            }
        }
        check("Coffee and Energy Drink were used, the key was not", used == 2);
        check("Coffee healed, drink boosted", hurt.getHp() == 40 && hurt.getAttackBoost() == 1.5);
    }

    // ---------- 9. Player: party and "who fights first" ----------
    static void testPlayerParty() {
        System.out.println("--- Player: party ---");
        Player susie = new Player("Susie");
        check("Player name", susie.getName().equals("Susie"));
        check("Party starts empty", susie.getParty().size() == 0);
        check("No healthy LeMon when the party is empty", !susie.hasHealthyLeMon());
        check("First healthy LeMon is null when empty", susie.getFirstHealthyLeMon() == null);

        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        check("addLeMon puts it in the party (true)", susie.addLeMon(compy));
        susie.addLeMon(bugsy);
        check("Party has 2 LeMon", susie.getParty().size() == 2);
        check("First healthy is Compy", susie.getFirstHealthyLeMon() == compy);

        compy.takeDamage(100);
        check("When Compy faints, Bugsy is first healthy", susie.getFirstHealthyLeMon() == bugsy);
        check("Still has a healthy LeMon", susie.hasHealthyLeMon());
        bugsy.takeDamage(100);
        check("All fainted: no healthy LeMon", !susie.hasHealthyLeMon());
        check("All fainted: first healthy is null", susie.getFirstHealthyLeMon() == null);

        susie.getParty().clear();
        check("Clearing the copy does NOT empty the real party", susie.getParty().size() == 2);
    }

    // ---------- 10. Player: party limit (2) and storage ----------
    static void testPlayerStorage() {
        System.out.println("--- Player: storage ---");
        Player susie = new Player("Susie");
        check("Party limit is 2", Player.PARTY_LIMIT == 2);
        check("1st LeMon joins the party", susie.addLeMon(new LeMon("A", Type.NORMAL, 10, 1)));
        check("2nd LeMon joins the party", susie.addLeMon(new LeMon("B", Type.NORMAL, 10, 1)));
        check("3rd LeMon goes to STORAGE (false)", !susie.addLeMon(new LeMon("C", Type.NORMAL, 10, 1)));
        check("4th LeMon goes to STORAGE (false)", !susie.addLeMon(new LeMon("D", Type.NORMAL, 10, 1)));
        check("Party is capped at 2", susie.getParty().size() == 2);
        check("Storage holds 2", susie.getStorage().size() == 2);
        check("The first stored one is C", susie.getStorage().get(0).getName().equals("C"));

        check("Swap works", susie.swapWithStorage(0, 0));
        check("C is now first in the party", susie.getParty().get(0).getName().equals("C"));
        check("A is now in storage", susie.getStorage().get(0).getName().equals("A"));
        check("Party size is still 2", susie.getParty().size() == 2);
        check("Bad party number is refused", !susie.swapWithStorage(9, 0));
        check("Bad storage number is refused", !susie.swapWithStorage(0, 9));
        check("Negative number is refused", !susie.swapWithStorage(-1, 0));
        check("Refused swaps change nothing", susie.getParty().get(0).getName().equals("C"));
    }

    // ---------- 11. Player: the bag and useItem ----------
    static void testPlayerBag() {
        System.out.println("--- Player: bag ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        susie.addLeMon(compy);
        check("Bag starts empty", susie.getBag().size() == 0);

        susie.addItem(new HealingItem("Coffee", 20));
        susie.addItem(new BattleItem("Energy Drink", 1.5));
        susie.addItem(new KeyItem("Rusty Key", "Opens the first locked door", "door1"));
        check("Bag has 3 items", susie.getBag().size() == 3);
        susie.getBag().clear();
        check("Clearing the copy does NOT empty the real bag", susie.getBag().size() == 3);

        compy.takeDamage(30);
        try {
            String msg = susie.useItem(0, compy);
            check("useItem heals: message", msg.equals("Compy recovered 20 HP!"));
            check("useItem heals: HP 10 -> 30", compy.getHp() == 30);
            check("Used item is REMOVED from the bag", susie.getBag().size() == 2);
            check("Next item is now the Energy Drink", susie.getBag().get(0).getName().equals("Energy Drink"));

            msg = susie.useItem(0, compy);
            check("Battle item message", msg.equals("Compy's attack rose!"));
            check("Attack boost is active", compy.getAttackBoost() == 1.5);
            check("Bag has 1 item left (the key)", susie.getBag().size() == 1);
        } catch (InvalidMoveException e) {
            check("valid useItem should not throw", false);
        }

        boolean keyRefused = false;
        try {
            susie.useItem(0, compy);
        } catch (InvalidMoveException e) {
            keyRefused = e.getMessage().equals("Rusty Key can't be used on Compy.");
        }
        check("Key item can't be used on a LeMon (exception)", keyRefused);
        check("A refused item is NOT removed", susie.getBag().size() == 1);

        boolean badIndex = false;
        try {
            susie.useItem(5, compy);
        } catch (InvalidMoveException e) {
            badIndex = e.getMessage().equals("That item does not exist: 6");
        }
        check("Bad item number throws InvalidMoveException", badIndex);

        susie.addItem(new HealingItem("Coffee", 20));
        compy.heal(100);
        boolean fullHp = false;
        try {
            susie.useItem(1, compy);
        } catch (InvalidMoveException e) {
            fullHp = true;
        }
        check("Coffee is refused at full HP", fullHp);
        check("Coffee is NOT wasted (still in the bag)", susie.getBag().size() == 2);

        compy.takeDamage(100);
        boolean fainted = false;
        try {
            susie.useItem(1, compy);
        } catch (InvalidMoveException e) {
            fainted = true;
        }
        check("Coffee is refused on a fainted LeMon", fainted);
        check("Still in the bag", susie.getBag().size() == 2);
    }

    // ---------- 12. Player: viewing and moving ----------
    static void testPlayerViewAndMove() {
        System.out.println("--- Player: view and move ---");
        Player susie = new Player("Susie");
        check("Empty party text", susie.viewLeMon().equals("You have no LeMon."));
        check("Empty bag text", susie.viewBag().equals("Your bag is empty."));

        susie.addLeMon(new LeMon("Compy", Type.HARDWARE, 40, 12));
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(bugsy);
        bugsy.takeDamage(100);
        check("viewLeMon text",
              susie.viewLeMon().equals("1. Compy (HARDWARE) HP 40/40\n2. Bugsy (SOFTWARE) HP 0/35 [fainted]"));

        susie.addItem(new HealingItem("Coffee", 20));
        susie.addItem(new KeyItem("Rusty Key", "Opens door 1", "door1"));
        check("viewBag text", susie.viewBag().equals("1. Coffee - Restores 20 HP\n2. Rusty Key - Opens door 1"));

        check("Starting location", susie.getLocation().equals("Start"));
        susie.move("Labyrinth 1st floor");
        check("move() changes the location", susie.getLocation().equals("Labyrinth 1st floor"));
    }

    // ---------- 13. Player + LeMon + Items together ----------
    static void testPlayerScenario() {
        System.out.println("--- Player scenario (everything together) ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        bugsy.addMove(new Move("Compile Error", Type.SOFTWARE, 8));
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        susie.addItem(new HealingItem("Coffee", 20));

        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 8);
        Move paperCut = new Move("Paper Cut", Type.PAPER, 20);

        // Rocky (PAPER) hits Compy (HARDWARE) super effectively: 20 x 2.0 = 40
        LeMon lead = susie.getFirstHealthyLeMon();
        System.out.println(rocky.attack(lead, paperCut));
        check("Compy fainted in one hit", compy.isFainted());

        // the next healthy LeMon steps up
        lead = susie.getFirstHealthyLeMon();
        check("Bugsy steps up", lead == bugsy);

        // Rocky (PAPER) vs Bugsy (SOFTWARE) is weak: 20 x 0.5 = 10
        System.out.println(rocky.attack(lead, paperCut));
        check("Bugsy has 25 HP", bugsy.getHp() == 25);

        try {
            System.out.println(susie.useItem(0, lead));
            check("Coffee heals only 10 (cap at max)", bugsy.getHp() == 35);
            check("Coffee is gone from the bag", susie.getBag().size() == 0);
            System.out.println(lead.attack(rocky, lead.getMove(0)));
            check("Bugsy (SOFTWARE) beats Rocky (PAPER): 8 x 2.0 = 16", rocky.getHp() == 44);
        } catch (InvalidMoveException e) {
            check("scenario should not throw", false);
        }
        check("Susie still has a healthy LeMon", susie.hasHealthyLeMon());
    }

    // ---------- 14. Battle: setup ----------
    static void testBattleSetup() {
        System.out.println("--- Battle: setup ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 8);

        Battle battle = new Battle(susie, rocky);
        check("Your first LeMon steps out", battle.getPlayerLeMon() == compy);
        check("The opponent is Rocky", battle.getOpponentLeMon() == rocky);
        check("Battle starts ONGOING", battle.getState() == Battle.State.ONGOING);
        check("Battle is not over", !battle.isOver());
        check("Battle starts on turn 1", battle.getTurn() == 1);

        ArrayList<String> log = battle.startBattle();
        check("startBattle gives 2 messages", log.size() == 2);
        check("Message 1", log.get(0).equals("A battle begins! Rocky appeared!"));
        check("Message 2", log.get(1).equals("Go, Compy!"));

        // the first LeMon has fainted: the next healthy one steps out
        compy.takeDamage(100);
        Battle second = new Battle(susie, rocky);
        check("A fainted LeMon is skipped", second.getPlayerLeMon() == bugsy);

        // every LeMon has fainted: you cannot fight
        bugsy.takeDamage(100);
        Battle hopeless = new Battle(susie, rocky);
        ArrayList<String> msg = hopeless.startBattle();
        check("No healthy LeMon: message", msg.get(0).equals("You have no LeMon able to fight!"));
        check("No healthy LeMon: battle is LOST", hopeless.getState() == Battle.State.LOST);
        check("No healthy LeMon: battle is over", hopeless.isOver());
    }

    // ---------- 15. Battle: checkWinner ----------
    static void testBattleCheckWinner() {
        System.out.println("--- Battle: checkWinner ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 8);

        Battle battle = new Battle(susie, rocky);
        check("Nobody fainted: no messages", battle.checkWinner().size() == 0);
        check("Nobody fainted: still ONGOING", battle.getState() == Battle.State.ONGOING);

        compy.takeDamage(100);
        ArrayList<String> log = battle.checkWinner();
        check("Your LeMon fainted: 2 messages", log.size() == 2);
        check("Faint message", log.get(0).equals("Compy fainted!"));
        check("You are told to choose", log.get(1).equals("Choose another LeMon!"));
        check("The battle now needs a switch", battle.needsSwitch());
        check("Battle is still ONGOING", battle.getState() == Battle.State.ONGOING);
        check("checkWinner does not repeat itself", battle.checkWinner().size() == 0);

        battle.switchLeMon(1);                                  // you choose Bugsy
        bugsy.takeDamage(100);
        log = battle.checkWinner();
        check("Last LeMon fainted: LOST", battle.getState() == Battle.State.LOST);
        check("Lost message", log.contains("You have no LeMon left... you lost!"));

        // a new battle where the enemy faints
        Player p2 = new Player("Susie");
        p2.addLeMon(new LeMon("Compy", Type.HARDWARE, 40, 12));
        LeMon enemy = new LeMon("Rocky", Type.PAPER, 60, 8);
        Battle b2 = new Battle(p2, enemy);
        enemy.takeDamage(100);
        log = b2.checkWinner();
        check("Enemy fainted: WON", b2.getState() == Battle.State.WON);
        check("Won message", log.get(0).equals("Rocky fainted! You won!"));
        check("Battle is over", b2.isOver());
        check("After the battle ends, checkWinner does nothing", b2.checkWinner().size() == 0);
    }

    // ---------- 16. Battle: fight and speed order ----------
    static void testBattleFight() {
        System.out.println("--- Battle: fight ---");

        // --- the ENEMY is faster (15 vs 12) ---
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        susie.addLeMon(compy);
        LeMon bug = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        bug.addMove(new Move("Compile Error", Type.SOFTWARE, 8));
        Battle battle = new Battle(susie, bug);

        ArrayList<String> log = battle.useMove(0);
        check("Faster enemy attacks FIRST", log.get(0).equals("Bugsy used Compile Error! 4 damage"));
        check("Then you attack", log.get(1).equals("Compy used System Crash! 18 damage"));
        check("Compy took 4 (40 -> 36)", compy.getHp() == 36);
        check("Bugsy took 18 (35 -> 17)", bug.getHp() == 17);
        check("The turn counter went up", battle.getTurn() == 2);

        log = battle.useMove(0);
        check("Winning move: 3 messages", log.size() == 3);
        check("Win message", log.get(2).equals("Bugsy fainted! You won!"));
        check("Battle is WON", battle.getState() == Battle.State.WON);
        check("Moves after the battle do nothing", battle.useMove(0).size() == 0);

        // --- YOU are faster (12 vs 8) ---
        Player p2 = new Player("Susie");
        LeMon hero = new LeMon("Compy", Type.HARDWARE, 40, 12);
        hero.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p2.addLeMon(hero);
        LeMon blob = new LeMon("Blob", Type.NORMAL, 30, 8);
        blob.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle b2 = new Battle(p2, blob);
        log = b2.useMove(0);
        check("Faster player attacks FIRST", log.get(0).equals("Compy used System Crash! 9 damage"));
        check("Then the enemy answers", log.get(1).equals("Blob used Slam! 6 damage"));

        // --- a speed TIE: the player goes first ---
        Player p3 = new Player("Susie");
        LeMon a = new LeMon("Compy", Type.HARDWARE, 40, 10);
        a.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p3.addLeMon(a);
        LeMon b = new LeMon("Blob", Type.NORMAL, 30, 10);
        b.addMove(new Move("Slam", Type.NORMAL, 6));
        log = new Battle(p3, b).useMove(0);
        check("Speed tie: the player goes first", log.get(0).startsWith("Compy used"));

        // --- a wrong move number costs no turn ---
        Player p4 = new Player("Susie");
        LeMon c = new LeMon("Compy", Type.HARDWARE, 40, 12);
        c.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p4.addLeMon(c);
        LeMon d = new LeMon("Blob", Type.NORMAL, 30, 8);
        d.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle b4 = new Battle(p4, d);
        log = b4.useMove(9);
        check("Bad move number: error message", log.size() == 1 && log.get(0).equals("That move does not exist: 10"));
        check("Bad move number: no damage to anyone", c.getHp() == 40 && d.getHp() == 30);
        check("Bad move number: the turn does not count", b4.getTurn() == 1);

        // --- executeTurn on its own: the enemy attacks ---
        log = b4.executeTurn();
        check("executeTurn: enemy attacks", log.get(0).equals("Blob used Slam! 6 damage"));
        check("executeTurn: your LeMon is hurt", c.getHp() == 34);

        // --- an enemy with no moves ---
        LeMon empty = new LeMon("Dummy", Type.NORMAL, 10, 1);
        Battle b5 = new Battle(p4, empty);
        check("Enemy with no moves", b5.executeTurn().get(0).equals("Dummy has no moves!"));

        // --- the enemy is faster and KNOCKS OUT your LeMon before it can move ---
        Player p6 = new Player("Susie");
        LeMon compy6 = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy6.addMove(new Move("System Crash", Type.HARDWARE, 9));
        LeMon bugsy6 = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        p6.addLeMon(compy6);
        p6.addLeMon(bugsy6);
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 20);
        rocky.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle b6 = new Battle(p6, rocky);
        log = b6.useMove(0);
        check("Knocked out first: enemy attack", log.get(0).equals("Rocky used Paper Cut! 40 damage"));
        check("Knocked out first: faint message", log.get(1).equals("Compy fainted!"));
        check("Knocked out first: you must choose", log.get(2).equals("Choose another LeMon!"));
        check("Knocked out first: Compy never got to attack", rocky.getHp() == 60 && log.size() == 3);
        check("The battle waits for your choice", b6.needsSwitch());

        // --- ...and when it was your last LeMon, you lose ---
        Player p7 = new Player("Susie");
        LeMon solo = new LeMon("Compy", Type.HARDWARE, 40, 12);
        solo.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p7.addLeMon(solo);
        LeMon rocky7 = new LeMon("Rocky", Type.PAPER, 60, 20);
        rocky7.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle b7 = new Battle(p7, rocky7);
        log = b7.useMove(0);
        check("Last LeMon knocked out: LOST", b7.getState() == Battle.State.LOST);
        check("Last LeMon knocked out: it never attacked", rocky7.getHp() == 60);
        check("Lost message", log.contains("You have no LeMon left... you lost!"));
    }

    // ---------- 17. Battle: the bag ----------
    static void testBattleBag() {
        System.out.println("--- Battle: bag ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        susie.addLeMon(compy);
        susie.addItem(new HealingItem("Coffee", 20));
        susie.addItem(new BattleItem("Energy Drink", 1.5));
        susie.addItem(new KeyItem("Rusty Key", "Opens the first locked door", "door1"));
        LeMon blob = new LeMon("Blob", Type.NORMAL, 100, 8);
        blob.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle battle = new Battle(susie, blob);

        compy.takeDamage(30);                                  // Compy has 10 HP
        ArrayList<String> log = battle.useItem(0);             // Coffee
        check("Item message comes first", log.get(0).equals("Compy recovered 20 HP!"));
        check("Then the enemy answers", log.get(1).equals("Blob used Slam! 6 damage"));
        check("HP: 10 + 20 - 6 = 24", compy.getHp() == 24);
        check("Coffee left the bag", susie.getBag().size() == 2);
        check("Using an item costs the turn", battle.getTurn() == 2);

        log = battle.useItem(0);                               // Energy Drink
        check("Battle item message", log.get(0).equals("Compy's attack rose!"));
        check("The attack boost is active", compy.getAttackBoost() == 1.5);

        int hpBefore = compy.getHp();
        int turnBefore = battle.getTurn();
        log = battle.useItem(0);                               // Rusty Key: refused
        check("Key refused: message", log.size() == 1 && log.get(0).equals("Rusty Key can't be used on Compy."));
        check("Key refused: the enemy does NOT attack", compy.getHp() == hpBefore);
        check("Key refused: the turn does not count", battle.getTurn() == turnBefore);
        check("Key refused: still in the bag", susie.getBag().size() == 1);

        log = battle.useItem(7);
        check("Bad item number", log.size() == 1 && log.get(0).equals("That item does not exist: 8"));
        check("Bad item number: the turn does not count", battle.getTurn() == turnBefore);
    }

    // ---------- 18. Battle: switching LeMon ----------
    static void testBattleSwitch() {
        System.out.println("--- Battle: switch ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        LeMon blob = new LeMon("Blob", Type.NORMAL, 100, 8);
        blob.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle battle = new Battle(susie, blob);

        ArrayList<String> log = battle.switchLeMon(1);
        check("Switch message", log.get(0).equals("Go, Bugsy!"));
        check("The enemy answers", log.get(1).equals("Blob used Slam! 6 damage"));
        check("Bugsy is now fighting", battle.getPlayerLeMon() == bugsy);
        check("Bugsy took the hit (35 -> 29)", bugsy.getHp() == 29);
        check("Switching costs the turn", battle.getTurn() == 2);

        log = battle.switchLeMon(1);
        check("Already fighting: refused", log.size() == 1 && log.get(0).equals("Bugsy is already fighting!"));
        check("Already fighting: no enemy attack", bugsy.getHp() == 29);

        compy.takeDamage(100);
        log = battle.switchLeMon(0);
        check("Fainted LeMon can't be chosen", log.get(0).equals("Compy has fainted and can't fight!"));

        log = battle.switchLeMon(9);
        check("Bad slot", log.get(0).equals("No LeMon in that slot: 10"));
        check("Refused switches do not use the turn", battle.getTurn() == 2);
    }

    // ---------- 18b. Battle: the forced swap ----------
    static void testBattleForcedSwap() {
        System.out.println("--- Battle: forced swap ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        bugsy.addMove(new Move("Compile Error", Type.SOFTWARE, 8));
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        susie.addItem(new HealingItem("Coffee", 20));
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 20);
        rocky.addMove(new Move("Paper Cut", Type.PAPER, 20));     // knocks Compy out in one hit
        Battle battle = new Battle(susie, rocky);

        battle.useMove(0);                                        // Rocky is faster and knocks Compy out
        check("Compy fainted", compy.isFainted());
        check("The battle needs a switch", battle.needsSwitch());
        check("Compy stays on the field until you choose", battle.getPlayerLeMon() == compy);
        int turnBefore = battle.getTurn();
        int rockyHp = rocky.getHp();

        // everything except switching is refused
        ArrayList<String> log = battle.useMove(0);
        check("Fight is refused", log.size() == 1 && log.get(0).equals("You must choose another LeMon first!"));
        log = battle.useItem(0);
        check("Bag is refused", log.get(0).equals("You must choose another LeMon first!"));
        check("Coffee was not used up", susie.getBag().size() == 1);
        check("Refused actions change nothing", battle.getTurn() == turnBefore && rocky.getHp() == rockyHp);

        // wrong choices are refused and you still must choose
        log = battle.switchLeMon(0);
        check("Can't pick the fainted LeMon", log.get(0).equals("Compy has fainted and can't fight!"));
        log = battle.switchLeMon(9);
        check("Can't pick a missing slot", log.get(0).equals("No LeMon in that slot: 10"));
        check("You still must switch", battle.needsSwitch());

        // a valid choice is a FREE swap: the enemy does not attack
        int bugsyHp = bugsy.getHp();
        log = battle.switchLeMon(1);
        check("Forced swap: one message", log.size() == 1 && log.get(0).equals("Go, Bugsy!"));
        check("Forced swap: the enemy does NOT attack", bugsy.getHp() == bugsyHp);
        check("Forced swap does not use a turn", battle.getTurn() == turnBefore);
        check("Bugsy is fighting", battle.getPlayerLeMon() == bugsy);
        check("No longer needs a switch", !battle.needsSwitch());

        // you can fight again (Rocky is still faster: 20 vs 15)
        log = battle.useMove(0);
        check("Fighting works again: 2 messages", log.size() == 2);
        check("Rocky hits Bugsy (weak): 10", log.get(0).equals("Rocky used Paper Cut! 10 damage"));
        check("Bugsy hits Rocky (strong): 16", log.get(1).equals("Bugsy used Compile Error! 16 damage"));

        // the LAST LeMon faints: nobody is left, so you lose
        bugsy.takeDamage(bugsy.getHp() - 5);                      // Bugsy has 5 HP left
        log = battle.useMove(0);                                  // Rocky is faster and knocks Bugsy out
        check("Everyone fainted: you lost", battle.getState() == Battle.State.LOST);
        check("After losing, no actions work", battle.useMove(0).size() == 0 && battle.switchLeMon(0).size() == 0);
    }

    // ---------- 19. Battle: rewards and cleanup ----------
    static void testBattleRewards() {
        System.out.println("--- Battle: rewards ---");

        // win WITH a reward
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        susie.addLeMon(compy);
        LeMon weak = new LeMon("Weakling", Type.NORMAL, 1, 1);
        weak.addMove(new Move("Poke", Type.NORMAL, 1));
        Battle win = new Battle(susie, weak, new HealingItem("Coffee", 20));
        ArrayList<String> log = win.useMove(0);
        check("Won", win.getState() == Battle.State.WON);
        check("Reward message", log.get(log.size() - 1).equals("You got Coffee!"));
        check("Reward is in the bag", susie.getBag().size() == 1 && susie.getBag().get(0).getName().equals("Coffee"));

        // win WITHOUT a reward
        Player p2 = new Player("Susie");
        LeMon c2 = new LeMon("Compy", Type.HARDWARE, 40, 12);
        c2.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p2.addLeMon(c2);
        LeMon weak2 = new LeMon("Weakling", Type.NORMAL, 1, 1);
        weak2.addMove(new Move("Poke", Type.NORMAL, 1));
        new Battle(p2, weak2).useMove(0);
        check("No reward: the bag stays empty", p2.getBag().size() == 0);

        // NO reward when you lose
        Player p3 = new Player("Susie");
        LeMon c3 = new LeMon("Compy", Type.HARDWARE, 40, 12);
        c3.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p3.addLeMon(c3);
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 20);
        rocky.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle lose = new Battle(p3, rocky, new HealingItem("Coffee", 20));
        lose.useMove(0);
        check("Lost the battle", lose.getState() == Battle.State.LOST);
        check("No reward for losing", p3.getBag().size() == 0);

        // the attack boost is removed when the battle ends
        Player p4 = new Player("Susie");
        LeMon c4 = new LeMon("Compy", Type.HARDWARE, 40, 12);
        c4.addMove(new Move("System Crash", Type.HARDWARE, 9));
        p4.addLeMon(c4);
        p4.addItem(new BattleItem("Energy Drink", 1.5));
        LeMon enemy = new LeMon("Blob", Type.NORMAL, 20, 8);
        enemy.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle b4 = new Battle(p4, enemy);
        b4.useItem(0);
        check("Boosted during the battle", c4.getAttackBoost() == 1.5);
        b4.useMove(0);
        b4.useMove(0);
        check("The battle was won", b4.getState() == Battle.State.WON);
        check("Boost is reset after the battle", c4.getAttackBoost() == 1.0);
    }

    // ---------- 20. Battle: a whole fight, start to finish ----------
    static void testBattleFullFight() {
        System.out.println("--- Battle: a whole fight ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        bugsy.addMove(new Move("Compile Error", Type.SOFTWARE, 8));
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        susie.addItem(new HealingItem("Coffee", 20));

        LeMon rocky = new LeMon("Rocky", Type.PAPER, 60, 8);
        rocky.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle battle = new Battle(susie, rocky, new BattleItem("Energy Drink", 1.5));

        for (String line : battle.startBattle()) System.out.println(line);
        int guard = 0;
        while (!battle.isOver() && guard < 30) {
            if (battle.needsSwitch()) {
                // our LeMon fainted: we MUST choose the next one (here: the first healthy one)
                ArrayList<LeMon> party = susie.getParty();
                for (int i = 0; i < party.size(); i++) {
                    if (!party.get(i).isFainted()) {
                        for (String line : battle.switchLeMon(i)) System.out.println(line);
                        break;
                    }
                }
            } else {
                for (String line : battle.useMove(0)) System.out.println(line);
            }
            guard++;
        }
        check("The fight ended", battle.isOver());
        check("It did not take forever", guard < 30);
        check("Susie won with Bugsy", battle.getState() == Battle.State.WON && !bugsy.isFainted());
        check("Compy fainted on the way", compy.isFainted());
        check("The reward is in the bag", susie.getBag().size() == 2);
    }

    // ---------- 8b. ReviveItem ----------
    static void testReviveItem() {
        System.out.println("--- ReviveItem ---");
        Item revive = new ReviveItem("Revive", 0.5);
        check("ReviveItem is an Item", revive instanceof Item);
        check("Revive description", revive.getDescription().equals("Revives a fainted LeMon with 50% HP"));

        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        check("Revive can't be used on a healthy LeMon", !revive.canUseOn(compy));
        compy.takeDamage(30);
        check("Revive can't be used on a hurt (but awake) LeMon", !revive.canUseOn(compy));
        compy.takeDamage(100);
        check("Revive CAN be used on a fainted LeMon", revive.canUseOn(compy));
        check("Revive message", revive.use(compy).equals("Compy was revived with 20 HP!"));
        check("Revived with half HP (20 of 40)", compy.getHp() == 20);
        check("Compy is awake again", !compy.isFainted());
        check("Revive can't be used again on it", !revive.canUseOn(compy));

        LeMon tiny = new LeMon("Tiny", Type.NORMAL, 1, 1);
        tiny.takeDamage(1);
        revive.use(tiny);
        check("Revive always gives at least 1 HP", tiny.getHp() == 1);

        // through the Player's bag
        Player susie = new Player("Susie");
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(bugsy);
        susie.addItem(new ReviveItem("Revive", 0.5));
        boolean refused = false;
        try {
            susie.useItem(0, bugsy);
        } catch (InvalidMoveException e) {
            refused = e.getMessage().equals("Revive can't be used on Bugsy.");
        }
        check("Bag: revive refused on an awake LeMon", refused);
        check("Bag: the revive item is NOT wasted", susie.getBag().size() == 1);
        bugsy.takeDamage(100);
        try {
            check("Bag: revive works on a fainted LeMon", susie.useItem(0, bugsy).equals("Bugsy was revived with 17 HP!"));
        } catch (InvalidMoveException e) {
            check("valid revive should not throw", false);
        }
        check("Bag: the revive item is used up", susie.getBag().size() == 0);
    }

    // ---------- 8c. LeMon.copy ----------
    static void testLeMonCopy() {
        System.out.println("--- LeMon copy ---");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        compy.takeDamage(15);
        compy.setAttackBoost(1.5);

        LeMon twin = compy.copy();
        check("Copy is a different object", twin != compy);
        check("Copy: same name and type", twin.getName().equals("Compy") && twin.getType() == Type.HARDWARE);
        check("Copy: same HP (25/40)", twin.getHp() == 25 && twin.getMaxHp() == 40);
        check("Copy: same speed", twin.getSpeed() == 12);
        check("Copy: same moves", twin.getMoves().size() == 1);
        check("Copy: same boost", twin.getAttackBoost() == 1.5);

        twin.takeDamage(10);
        check("Hurting the copy does NOT hurt the original", compy.getHp() == 25);
        compy.heal(100);
        check("Healing the original does NOT heal the copy", twin.getHp() == 15);

        LeMon fainted = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        fainted.takeDamage(100);
        check("A fainted LeMon copies as fainted", fainted.copy().isFainted());
    }

    // ---------- 8d. Checkpoints ----------
    static void testCheckpoints() {
        System.out.println("--- Checkpoints ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        susie.addItem(new HealingItem("Coffee", 20));

        check("No checkpoint at the start", !susie.hasCheckpoint());
        check("No checkpoint: location is null", susie.getLastCheckpointLocation() == null);

        // going back with NO checkpoint: the LeMon are just healed
        compy.takeDamage(100);
        bugsy.takeDamage(100);
        String msg = susie.returnToCheckpoint();
        check("No checkpoint: message", msg.equals("No checkpoint yet. Your LeMon were healed where you fell."));
        check("No checkpoint: LeMon are healed", susie.getParty().get(0).getHp() == 40 && susie.getParty().get(1).getHp() == 35);

        // a safe place heals and saves
        susie.getParty().get(0).takeDamage(30);
        msg = susie.reachCheckpoint("Floor 1 safe room");
        check("Reach checkpoint: message", msg.equals("Checkpoint saved at Floor 1 safe room."));
        check("Reach checkpoint: location", susie.getLocation().equals("Floor 1 safe room"));
        check("Reach checkpoint: the party is healed", susie.getParty().get(0).getHp() == 40);
        check("There is a checkpoint now", susie.hasCheckpoint());
        check("Checkpoint location", susie.getLastCheckpointLocation().equals("Floor 1 safe room"));

        // the game moves on...
        susie.move("Floor 2");
        susie.getParty().get(0).takeDamage(100);               // Compy faints
        susie.getParty().get(1).takeDamage(20);                // Bugsy is hurt
        try {
            susie.useItem(0, susie.getParty().get(1));         // the Coffee is used up
        } catch (InvalidMoveException e) {
            check("coffee should work", false);
        }
        susie.addItem(new KeyItem("Rusty Key", "Opens door 1", "door1"));   // a new item found later
        check("Before going back: Compy is fainted", susie.getParty().get(0).isFainted());
        check("Before going back: bag is Key only", susie.getBag().size() == 1);

        // ...then we go back
        msg = susie.returnToCheckpoint();
        check("Return message", msg.equals("You returned to your last checkpoint: Floor 1 safe room."));
        check("Back at the checkpoint location", susie.getLocation().equals("Floor 1 safe room"));
        check("Compy is back at full HP", susie.getParty().get(0).getHp() == 40);
        check("Bugsy is back at full HP", susie.getParty().get(1).getHp() == 35);
        check("The Coffee is back in the bag", susie.getBag().size() == 1 && susie.getBag().get(0).getName().equals("Coffee"));
        check("The key found later is gone", susie.getBag().get(0) instanceof HealingItem);
        check("Party still has 2 LeMon", susie.getParty().size() == 2);

        // the same checkpoint works again and again
        susie.getParty().get(0).takeDamage(100);
        susie.returnToCheckpoint();
        check("Loading the checkpoint a 2nd time works", susie.getParty().get(0).getHp() == 40);

        // a newer checkpoint replaces the older one
        susie.reachCheckpoint("Floor 2 safe room");
        check("Newer checkpoint replaces the old one", susie.getLastCheckpointLocation().equals("Floor 2 safe room"));
        susie.move("Floor 3");
        susie.returnToCheckpoint();
        check("Return goes to the NEWEST checkpoint", susie.getLocation().equals("Floor 2 safe room"));

        // manual save does not heal
        susie.getParty().get(0).takeDamage(10);
        susie.saveCheckpoint();
        susie.getParty().get(0).takeDamage(10);
        susie.returnToCheckpoint();
        check("saveCheckpoint keeps the HP you had (30)", susie.getParty().get(0).getHp() == 30);

        // safety: a checkpoint saved with everybody fainted never leaves you stuck
        Player p2 = new Player("Susie");
        p2.addLeMon(new LeMon("Compy", Type.HARDWARE, 40, 12));
        p2.getParty().get(0).takeDamage(100);
        p2.saveCheckpoint();
        p2.returnToCheckpoint();
        check("Never come back with nobody able to fight", p2.hasHealthyLeMon());
    }

    // ---------- 20b. Battle: losing sends you to the checkpoint ----------
    static void testBattleLossCheckpoint() {
        System.out.println("--- Battle: losing ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        susie.addItem(new HealingItem("Coffee", 20));
        susie.reachCheckpoint("Safe room");

        susie.move("Floor 2");
        susie.addItem(new BattleItem("Energy Drink", 1.5));         // found after the checkpoint
        LeMon rocky = new LeMon("Rocky", Type.PAPER, 100, 20);
        rocky.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle battle = new Battle(susie, rocky, new ReviveItem("Revive", 0.5));

        // Rocky is faster: Compy goes down at once, we send Bugsy, then Bugsy goes down too
        battle.useMove(0);
        check("Compy fainted, we must choose", battle.needsSwitch());
        battle.switchLeMon(1);
        ArrayList<String> log = battle.useMove(0);
        // Bugsy has 35 HP and takes 10 per hit; keep fighting until all are down
        int guard = 0;
        while (!battle.isOver() && guard < 20) {
            if (battle.needsSwitch()) {
                battle.switchLeMon(1);
            } else {
                log = battle.useMove(0);
            }
            guard++;
        }
        // Bugsy has no moves, so useMove only gives an error; so make Bugsy faint directly
        if (!battle.isOver()) {
            battle.getPlayerLeMon().takeDamage(100);
            log = battle.checkWinner();
        }
        check("The battle was lost", battle.getState() == Battle.State.LOST);
        check("Lost message", log.contains("You have no LeMon left... you lost!"));
        check("Checkpoint message", log.contains("You returned to your last checkpoint: Safe room."));
        check("Back at the checkpoint", susie.getLocation().equals("Safe room"));
        check("LeMon are back and able to fight", susie.hasHealthyLeMon());
        check("Compy is back at full HP", susie.getParty().get(0).getHp() == 40);
        check("The item found after the checkpoint is gone", susie.getBag().size() == 1);
        check("Only the Coffee from before is left", susie.getBag().get(0).getName().equals("Coffee"));
        check("No reward for losing", !susie.getBag().get(0).getName().equals("Revive"));

        // losing with NO checkpoint: LeMon are healed, you stay put
        Player p2 = new Player("Susie");
        LeMon c2 = new LeMon("Compy", Type.HARDWARE, 40, 12);
        p2.addLeMon(c2);
        p2.move("Somewhere");
        LeMon rocky2 = new LeMon("Rocky", Type.PAPER, 100, 20);
        rocky2.addMove(new Move("Paper Cut", Type.PAPER, 20));
        Battle b2 = new Battle(p2, rocky2);
        c2.takeDamage(100);
        ArrayList<String> log2 = b2.checkWinner();
        check("No checkpoint: lost", b2.getState() == Battle.State.LOST);
        check("No checkpoint: healed message", log2.contains("No checkpoint yet. Your LeMon were healed where you fell."));
        check("No checkpoint: you stay where you are", p2.getLocation().equals("Somewhere"));
        check("No checkpoint: LeMon can fight again", p2.hasHealthyLeMon());

        // starting a battle with nobody able to fight also sends you back
        Player p3 = new Player("Susie");
        p3.addLeMon(new LeMon("Compy", Type.HARDWARE, 40, 12));
        p3.reachCheckpoint("Safe room");
        p3.move("Floor 3");
        p3.getParty().get(0).takeDamage(100);
        ArrayList<String> log3 = new Battle(p3, rocky2).startBattle();
        check("Battle with nobody able to fight: sent back", log3.contains("You returned to your last checkpoint: Safe room."));
        check("Battle with nobody able to fight: location", p3.getLocation().equals("Safe room"));
    }

    // ---------- 18c. Battle: using an item on a chosen LeMon ----------
    static void testBattleItemTarget() {
        System.out.println("--- Battle: item on a chosen LeMon ---");
        Player susie = new Player("Susie");
        LeMon compy = new LeMon("Compy", Type.HARDWARE, 40, 12);
        compy.addMove(new Move("System Crash", Type.HARDWARE, 9));
        LeMon bugsy = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        susie.addLeMon(compy);
        susie.addLeMon(bugsy);
        bugsy.takeDamage(100);                                    // Bugsy is fainted on the bench
        susie.addItem(new ReviveItem("Revive", 0.5));
        susie.addItem(new ReviveItem("Revive", 0.5));
        susie.addItem(new HealingItem("Coffee", 20));
        LeMon blob = new LeMon("Blob", Type.NORMAL, 100, 8);
        blob.addMove(new Move("Slam", Type.NORMAL, 6));
        Battle battle = new Battle(susie, blob);

        ArrayList<String> log = battle.useItem(0, 1);             // Revive -> Bugsy (slot 2)
        check("Revive a LeMon on the bench", log.get(0).equals("Bugsy was revived with 17 HP!"));
        check("Bugsy is awake again", !bugsy.isFainted() && bugsy.getHp() == 17);
        check("Then the enemy hits the LeMon that is fighting", log.get(1).equals("Blob used Slam! 6 damage") && compy.getHp() == 34);
        check("It cost the turn", battle.getTurn() == 2);
        check("The fighter did not change", battle.getPlayerLeMon() == compy);

        int turnBefore = battle.getTurn();
        log = battle.useItem(0, 0);                               // Revive -> Compy (awake): refused
        check("Revive refused on an awake LeMon", log.size() == 1 && log.get(0).equals("Revive can't be used on Compy."));
        check("Refused: no enemy attack and no turn", compy.getHp() == 34 && battle.getTurn() == turnBefore);
        check("Refused: the item stays", susie.getBag().size() == 2);

        log = battle.useItem(0, 9);
        check("Bad LeMon slot", log.get(0).equals("No LeMon in that slot: 10"));

        log = battle.useItem(1);                                  // Coffee on the fighter (no slot given)
        check("useItem(number) still targets the fighter", log.get(0).equals("Compy recovered 6 HP!") || log.get(0).startsWith("Compy recovered"));
    }


    static void testEverythingTogether() {
        System.out.println("--- Mini battle (everything together) ---");
        LeMon a = new LeMon("Compy", Type.HARDWARE, 40, 12);
        LeMon b = new LeMon("Bugsy", Type.SOFTWARE, 35, 15);
        a.addMove(new Move("System Crash", Type.HARDWARE, 9));
        a.addMove(new HealMove("Recover", 12));
        b.addMove(new Move("Compile Error", Type.SOFTWARE, 8));

        int rounds = 0;
        try {
            while (!a.isFainted() && !b.isFainted() && rounds < 50) {
                LeMon first  = a.isFasterThan(b) ? a : b;       // faster LeMon goes first
                LeMon second = (first == a) ? b : a;
                System.out.println(first.attack(second, first.getMove(0)));
                if (!second.isFainted()) {
                    System.out.println(second.attack(first, second.getMove(0)));
                }
                rounds++;
            }
        } catch (InvalidMoveException e) {
            check("battle should not throw", false);
        }

        check("Battle ended in 2 rounds", rounds == 2);
        check("Exactly one LeMon fainted", a.isFainted() != b.isFainted());
        check("Bugsy fainted, Compy won", b.isFainted() && !a.isFainted());
        check("Compy has 32 HP left (took 4 + 4)", a.getHp() == 32);
    }
}
