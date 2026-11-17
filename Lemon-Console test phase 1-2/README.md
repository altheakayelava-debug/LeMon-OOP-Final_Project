# LeMon: Labyrinth Explorers Monsters

A console game built from the split Java code, with **Phase 1: The Invitation**
and **Phase 2: The First Floor** implemented.

## What Phase 1 contains

Phase 1 is dialogue only. It follows Susie from the front gate of Gearson to the
office of Hans, the CEO, and ends with one choice:

| Choice | Result |
| --- | --- |
| `Who wouldn't?` | Hans gives Susie the coordinates. **Phase 1 Complete**, hooks into Phase 2. |
| `Hell nah.` | **Secret Ending 1 - "The Explorer Who Said No"**. |

## What Phase 2 contains

Phase 2 is the first floor of the labyrinth, using the existing `Battle`,
`Player`, `LeMon`, item, and checkpoint code. Susie enters with her partner Mya,
survives a tutorial battle, finds a stone gate with a crown-shaped slot, takes
the left path, defeats the floor guardian, opens the gate, and meets the crazy
man below.

A few things to know:

- The tutorial battle is mandatory; it teaches `Fight`, `Bag`, and the type chart
  (`HARDWARE > SOFTWARE > PAPER > HARDWARE`, `NORMAL` is neutral).
- The gate cannot be forced open. The left path has three rooms.
- The guardian must be beaten. Studying it or walking up to it head-on is safe,
  but the fight only starts when Susie throws a stone at the stalactite.
- Losing a battle returns Susie to the last checkpoint:
  `Labyrinth Entrance` or `Guardian's Antechamber`.
- The crown is a key item for the gate. After it opens, the crazy man must be
  talked to. His cipher lines are ROT13 and stay exactly as written.
- Phase 2 ends with the **PHASE 2 COMPLETE** banner and the Phase 3 hook.

The text is lightly polished from the Phase 2 document: typos and grammar are
fixed, but the voice, jokes, and the ROT13 cipher are unchanged.

## Files

| File | Purpose |
| --- | --- |
| `Main.java` | Starts the console menu (play either phase, both, run the tests, exit). |
| `Console.java` | Console screen: printing, wrapping, numbered choices, pauses. |
| `PhaseOne.java` | The Phase 1 story: scenes, dialogue, and the two endings. |
| `PhaseTwo.java` | The Phase 2 story: the first floor, battles, guardian, and ending. |
| `Tests.java` | The existing tests. They still run from the menu. |
| all other `.java` files | The existing game code, unchanged. |

The stories only talk to `Console`, and they use a `Player` object for Susie,
her party, her bag, and her current location. That keeps the story separate from
the screen and ready for a window (Swing/JavaFX) later.

## How to compile and run

From this folder:

```bash
javac *.java
java Main
```

Type a number and press ENTER to pick a menu option. During the story, press
ENTER to continue past the title screens, checkpoints, and endings.

Requires Java 8 or newer.

## How to play just the story

Pick `[1] Play Phase 1 - The Invitation` or `[2] Play Phase 2 - The First Floor`.
Pick `[3] Play both phases` to start at Phase 1 and continue into Phase 2 after
Susie accepts Hans's offer. Pick `[4] Run the tests` to run the 311 existing
checks.
