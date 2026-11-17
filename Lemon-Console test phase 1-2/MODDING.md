# Modding LeMon

This guide shows how to change the game without touching its rules. It assumes you
have read `README.md` and can compile with `javac *.java`.

## The one rule

Story code (`PhaseOne`, `PhaseTwo`, `Main`) only **talks to `Console`** and asks
`Battle` / `Player` / `LeMon` to do the work. Rules never live in a story class,
and printing never lives in a rules class. Keep it that way and any scene can move
to a window later without changes.

## Project map

| File | What it owns |
| --- | --- |
| `Main.java` | The menu and which phase runs. |
| `Console.java` | Reading input, wrapping text, menus, pauses, banners. |
| `PhaseOne.java` | The Phase 1 story and its two endings. |
| `PhaseTwo.java` | The first floor: Mya, battles, guardian, crown, crazy man. |
| `Player.java` | Susie's party, storage, bag, location, and checkpoints. |
| `LeMon.java` | A monster: name, type, HP, speed, and up to 4 moves. |
| `Move.java` | One attack: name, type, power. |
| `HealMove.java` | A move that heals its user instead of attacking. |
| `Type.java` | The type chart. |
| `Item.java` | The item base class. |
| `HealingItem.java` | Restores HP. |
| `ReviveItem.java` | Brings a fainted LeMon back. |
| `BattleItem.java` | Raises attack for one battle. |
| `KeyItem.java` | Opens a locked door; cannot be used on a LeMon. |
| `Battle.java` | One fight: turn order, rewards, switching, losing. |
| `Checkpoint.java` | A saved copy of the player's state. |
| `Tests.java` | The checks shown by the `Run the tests` menu option. |

## Add a LeMon

A LeMon needs a name, a type, max HP, and speed. Then give it moves:

```java
LeMon bat = new LeMon("Cave Bat", Type.PAPER, 22, 11);
bat.addMove(new Move("Screech", Type.PAPER, 7));
bat.addMove(new Move("Bite", Type.NORMAL, 5));
```

Rules:

- `PARTY_LIMIT` is **2**. `player.addLeMon(...)` returns `false` when the party is
  full and the LeMon goes to storage instead.
- `MAX_MOVES` is **4**. `addMove(...)` returns `false` after that.
- Speed only decides who attacks first. Ties go to the player.
- A LeMon at 0 HP has fainted and cannot act.

## Add a move

A normal move deals damage:

```java
mya.addMove(new Move("Mya Slam", Type.HARDWARE, 9));
```

Damage is `power × type multiplier × attack boost`, rounded down, with a minimum
of 1. The type chart is:

| Attacker | Strong against (×2) | Weak against (×0.5) |
| --- | --- | --- |
| `HARDWARE` | `SOFTWARE` | `PAPER` |
| `SOFTWARE` | `PAPER` | `HARDWARE` |
| `PAPER` | `HARDWARE` | `SOFTWARE` |
| `NORMAL` | nothing | nothing (always ×1) |

To add a new type, add it to the `Type` enum and to
`Type.multiplierAgainst(...)`.

A healing move is a child of `Move`:

```java
mya.addMove(new HealMove("Recover", 12));
```

## Add an item

Items are data first, so a new healing item is one line:

```java
susie.addItem(new HealingItem("Coffee", 20));
susie.addItem(new ReviveItem("Phoenix Pin", 0.5));
susie.addItem(new BattleItem("Overclock", 1.5));
susie.addItem(new KeyItem("Crown", "Fits the stone gate", "crown"));
```

For a completely new behaviour, extend `Item` and override `canUseOn` and `use`:

```java
public class Antidote extends Item {
    public Antidote() {
        super("Antidote", "Cures poison");
    }

    @Override
    public boolean canUseOn(LeMon target) {
        return !target.isFainted();
    }

    @Override
    public String use(LeMon target) {
        return target.getName() + " feels better!";
    }
}
```

Names must be different from every other file/class in the folder.

## Add or edit a scene

Scenes are just calls to `Console`:

```java
Console.scene("The Left Path - Bloodied Hall");
Console.narrate("The blood trail is thin and old.");
Console.action("Susie checks behind a fallen stone.");
Console.say("SUSIE", "scared", "A BLOOD TRAIL?!");
Console.say("MYA", "Myaa...");
Console.say("SUSIE", "I should keep moving.");
Console.pause();
```

Helpers:

- `Console.p("one", "two")` joins paragraphs with a blank line.
- `Console.choose(question, option1, option2, ...)` returns **1-based** numbers.
- `Console.message(text)` prints game information (battle log, item found,
  checkpoint).
- `Console.write(text)` prints one raw line, used by the battle header.
- Emotion is optional; pass `null` or use the two-argument `say`.

To add a room to Phase 2, add a method to `PhaseTwo` and call it from `play()`:

```java
private static void sideRoom(Player susie) {
    Console.scene("The Left Path - Side Room");
    Console.say("SUSIE", "happy", "Another room. Let's look around.");
    if (Console.choose("What does Susie do?", "Search", "Leave") == 1) {
        susie.addItem(new HealingItem("Coffee", 20));
        Console.message("Susie found a Coffee.");
    }
}
```

## Add a battle

`Battle` returns `String` messages and never prints. The Phase 2 file already has
a small battle screen you can copy (`runBattle`, `showBattle`, `chooseMove`,
`useBagInBattle`, `choosePartyMember`). A minimal fight is:

```java
LeMon imp = new LeMon("Glitch Imp", Type.NORMAL, 18, 8);
imp.addMove(new Move("Bite", Type.NORMAL, 5));

Battle battle = new Battle(susie, imp, new HealingItem("Coffee", 20));
for (String line : battle.startBattle()) {
    Console.message(line);
}

while (!battle.isOver()) {
    if (battle.needsSwitch()) {
        // ask the player for a healthy party member, then:
        // battle.switchLeMon(index)
        continue;
    }
    int choice = Console.choose("What will Susie do?", "Fight", "Bag");
    ArrayList<String> log;
    if (choice == 1) {
        log = battle.useMove(0);          // pick the move properly in real code
    } else {
        log = battle.useItem(0);          // pick the item properly in real code
    }
    for (String line : log) {
        Console.message(line);
    }
}
```

Important details:

- The Phase 2 helpers add `Back to Fight / Bag` to the move and item menus.
  Choosing it returns `-1` / `null`, so no turn is used and the main menu appears
  again.
- `startBattle()` can immediately end the fight if nobody can battle.
- The third `Battle` argument is the reward. It is added to the bag only on a win.
- `useMove` / `useItem` return messages even when the choice is invalid; an invalid
  choice costs no turn.
- When a LeMon faints and another is healthy, `needsSwitch()` becomes true. A forced
  switch is free; a normal switch costs a turn.
- Losing calls `player.returnToCheckpoint()` inside `checkWinner()`.
- Guard `battle.getPlayerLeMon()` before showing its HP if you call it outside the
  loop.

## Checkpoints and losing

```java
susie.move("Guardian's Antechamber");
Console.message(susie.reachCheckpoint("Guardian's Antechamber")); // heals + saves
```

- `reachCheckpoint(place)` heals all LeMon, moves Susie, and saves.
- `saveCheckpoint()` saves the current state without healing or moving.
- `returnToCheckpoint()` restores location, party, storage, and bag. Checkpoints
  store copies, so later changes do not leak into the save.
- Only the newest checkpoint is kept. A checkpoint replaces the older one.
- Items are stored by reference, which is fine because items never change.

## Tune Phase 2 balance

Current numbers:

| Side | Name | Type | HP | Speed | Moves |
| --- | --- | --- | --- | --- | --- |
| Player | Mya | `HARDWARE` | 40 | 13 | `Mya Slam` 9, `Recover` 12 |
| Enemy | Glitch Imp | `NORMAL` | 18 | 8 | `Bite` 5 |
| Boss | Crowned Guardian | `SOFTWARE` | 55 | 12 | `Crown Bash` 10, `Stone Grip` 14 |

Because Mya is `HARDWARE`, the guardian's `SOFTWARE` move is weak (×0.5) and Mya's
`HARDWARE` attack is strong (×2). The guardian is slower, so Mya always moves first.
`Stone Grip` is `NORMAL`, so it is the real threat.

Tips:

- Raising boss HP changes how many turns the player must survive.
- Raising boss damage makes healing and Coffee more important.
- If a fight becomes impossible, lower HP first, then damage.
- The Phase 2 boss is meant to be losable: losing restores the
  `Guardian's Antechamber` checkpoint and prints Susie's nervous line.
- Test a fight at least three times; `Battle` picks enemy moves randomly.

## Add a phase

1. Create `PhaseThree.java` with `public static void play()`.
2. Add a menu option in `Main.java`.
3. End the previous phase with a banner and let `Main` chain the phases.
4. Reuse `Player`, `LeMon`, `Battle`, items, and checkpoints; do not rewrite them.

`PhaseOne.play()` returns `true` when Susie accepts Hans's offer, which is how
`Main` knows whether to continue into Phase 2.

## Tests

`Tests.runAll()` is called by menu option `[4]`. It prints one `PASS` line per
check and ends with:

```
Result: 311 passed, 0 failed
```

When you add a rule, add a check in `Tests.java` next to the related tests. Keep
tests independent: create the objects they need inside the test method.

## Gotchas

- `Console.choose` returns option 1 when input ends (for example, when piped), so
  automated runs must supply every prompt in order.
- Items do not stack and have no quantity. Three Coffees are three bag entries.
- The bag lists every item, including key items, but `KeyItem.canUseOn` is always
  false.
- `LeMon.copy()` copies stats, HP, moves, and boost. Use it when you need an
  independent twin.
- Phase 2's battle helpers are `private`. To use them in another phase, either
  copy them or ask before refactoring them into a shared class.
- The ROT13 lines in Phase 2 decode to "DO NOT GO LEFT" and "I'LL BE WAITING FOR
  YOU BEHIND THE GATES." Keep the cipher text unchanged.

## Build and run

```bash
javac *.java
java Main
```

To build into a folder (keeps the source folder clean):

```bash
javac -encoding UTF-8 -d out *.java
java -cp out Main
```
