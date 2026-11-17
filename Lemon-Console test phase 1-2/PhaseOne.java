/**
 * PHASE 1 of LeMon: Labyrinth Explorers Monsters.
 *
 * This phase is dialogue only: no movement, no battles, no LeMon yet.
 * Susie is invited by Hans, the CEO of Gearson, to explore a newly
 * discovered labyrinth. At the end the player makes ONE choice:
 *
 *   1. "Who wouldn't?"  -> Hans gives her the coordinates (Phase 2 hook)
 *   2. "Hell nah."      -> ENDING 1, the secret ending
 *
 * Every line below is shown by Console, so this class only tells the story.
 */
public class PhaseOne {

    /**
     * Play the whole phase from the title screen to its ending.
     * Returns true when Susie accepts the invitation, so Phase 2 can follow.
     */
    public static boolean play() {
        Player susie = new Player("Susie");

        titleScreen();
        prologue(susie);
        frontGate(susie);
        theStairs(susie);
        topFloor(susie);
        hansOffice(susie);
        boolean accepted = lastQuestion(susie);

        Console.pause();
        return accepted;
    }

    // ---------- the beginning ----------
    private static void titleScreen() {
        Console.banner(
                "LeMon: Labyrinth Explorers Monsters",
                "",
                "Phase 1 - The Invitation",
                "A console story. No battles yet.");
        Console.pause();
    }

    private static void prologue(Player susie) {
        susie.move("Gearson - The City");
        Console.scene(susie.getLocation());
        Console.narrate(Console.p(
                "Susie is the most renowned labyrinth explorer in the world.",
                "So when Gearson, a famous technology company, sent her a letter about "
                        + "a newly discovered labyrinth, she read it twice and smiled once.",
                "The letter was signed by the CEO himself. That was unusual. CEOs do not "
                        + "usually invite explorers. They usually invite journalists.",
                "But Susie was curious. Curious enough to walk all the way to Gearson's "
                        + "headquarters on a bright, boring morning."));
        Console.action("Susie stops in front of the front gate and looks up at the building.");
    }

    // ---------- the guard ----------
    private static void frontGate(Player susie) {
        susie.move("Gearson Company - Front Gate");
        Console.scene(susie.getLocation());
        Console.say("GUARD", "HALT! Who are you? And what are your business in Gearson?");

        int pick = Console.choose("What does Susie say?",
                "\"Oh, hello there. I was invited here by the CEO.\"",
                "\"Here is the letter I received from him.\"");

        if (pick == 1) {
            Console.say("SUSIE", "happy",
                    "Oh, hello there. I was invited here by the CEO.");
            Console.say("GUARD", "Invited by the CEO, hm? Anyone can say that. Do you have proof?");
            Console.say("SUSIE", "happy", "Here is the letter I received from him.");
        } else {
            Console.say("SUSIE", "happy", "Here is the letter I received from him.");
        }

        Console.action("Susie hands the letter to the guard. He turns it over and checks the seal.");
        Console.say("GUARD", Console.p(
                "Hmm, it has the official seal of the company.",
                "Okay, you may go."));
        Console.say("SUSIE", "happy", "Thankie.");
    }

    // ---------- the stairs ----------
    private static void theStairs(Player susie) {
        susie.move("Gearson Company - The Stairs");
        Console.scene(susie.getLocation());
        Console.narrate(Console.p(
                "Susie enters the building.",
                "The lobby is all glass and polished marble. Somewhere above her, a "
                        + "ridiculous number of stairs is waiting.",
                "She starts climbing."));
        Console.say("SUSIE", "poker", Console.p(
                "For some reason, every CEO I know in the world has their office at the "
                        + "very top of the building.",
                "Maybe because they are at the top of the food chain... or maybe it's "
                        + "because they LOVE to look down on people.",
                "Either way, I don't really care. All people are equal in death."));
        Console.narrate("After climbing for a while, longer than she would ever admit, "
                + "Susie finally reaches the top floor.");
    }

    // ---------- the huge door ----------
    private static void topFloor(Player susie) {
        susie.move("Gearson Company - Top Floor");
        Console.scene(susie.getLocation());
        Console.narrate("A fancy, enormous door greets her. It is easily three times her height.");
        Console.say("SUSIE", "shocked", "W O A H, this door is huge!");

        Console.action("A maid suddenly opens the door, startling the already panicked Susie.");
        Console.say("MAID", Console.p(
                "Hello there, Madam Susie.",
                "Hans is expecting you. Please enter the room."));
        Console.say("SUSIE", "flustered", "O-okay...");

        Console.action("Susie steps into the room. The door closes behind her with a very "
                + "expensive sound.");
    }

    // ---------- the CEO ----------
    private static void hansOffice(Player susie) {
        susie.move("Gearson Company - Hans's Office");
        Console.scene(susie.getLocation());
        Console.narrate(Console.p(
                "The office is enormous. A wall of windows looks down on the entire city.",
                "Hans sits behind a desk that is probably older than the building."));

        Console.say("SUSIE", "happy",
                "Good day, Mister Hans! My name is Susie, the explorer you invited.");
        Console.action("Hans simply nods.");
        Console.say("SUSIE", "angry and nervous", Console.p(
                "JUST A NOD? SERIOUSLY? WHY ARE SOME CEOs LIKE THIS?",
                "I-if I may ask? When was the labyrinth discovered?"));

        Console.say("HANS", "poker",
                "It was only discovered last month, by a bunch of explorers like you.");
        Console.say("SUSIE", "poker",
                "I-I see. Well, why did you invite me if a bunch of explorers is already "
                        + "ahead of me?");

        Console.say("HANS", "poker", Console.p(
                "As you can see, all of them died.",
                "After they reported the appearance of the labyrinth to the Council, they "
                        + "immediately rushed to explore it. Obviously, they wanted to take "
                        + "the riches of the labyrinth.",
                "But after the Council sent a few cartographers to map it out, they "
                        + "discovered some of the bodies of the explorers who had reported "
                        + "to the Council.",
                "Still, the labyrinth may hold riches and wonders. And that's why I invited you.",
                "I want to explore this labyrinth, but I'm not an explorer. I'm just a "
                        + "humble CEO of a tech company."));

        Console.say("SUSIE", "shocked", Console.p(
                "Information overload... Ahhhh, my head is overheating.",
                "I see. Well, that sounds dangerous and exciting?"));

        Console.say("HANS", "poker", Console.p(
                "So, Susie. Let me ask you again.",
                "Do you really wanna explore this labyrinth, after learning that people "
                        + "died in there? Hm?"));
    }

    // ---------- the choice ----------
    /** The choice. Returns true when she accepts, false for the secret ending. */
    private static boolean lastQuestion(Player susie) {
        int pick = Console.choose("What does Susie answer?",
                "\"Who wouldn't?\"",
                "\"Hell nah.\"");

        if (pick == 1) {
            phaseTwoHook(susie);
            return true;
        } else {
            secretEnding(susie);
            return false;
        }
    }

    /** The normal path: Susie accepts, and Phase 2 will begin. */
    private static void phaseTwoHook(Player susie) {
        Console.say("SUSIE", "smiling", "Who wouldn't?");
        Console.say("HANS", "poker", Console.p(
                "Great.",
                "Here are the coordinates of the labyrinth. The transportation I hired "
                        + "for you is ready - you can go anytime."));
        Console.action("Hans slides a folded map across the desk. Susie picks it up but "
                + "does not open it yet. She already knows it will be the best thing she "
                + "has ever held.");
        Console.narrate(Console.p(
                "And so Susie leaves the office with a map in her pocket and a labyrinth "
                        + "waiting somewhere beyond the city.",
                "She does not know what is inside. She does not know who, or what, is "
                        + "already waiting for her there.",
                "But she is an explorer. And an explorer always goes in."));

        Console.banner(
                "PHASE 1 COMPLETE",
                "\"The Invitation\"",
                "",
                "Next: Phase 2 - The First Floor");
    }

    /** The secret ending: Susie refuses, and the labyrinth keeps waiting. */
    private static void secretEnding(Player susie) {
        Console.say("SUSIE", "poker", "...Hell nah.");
        Console.say("HANS", "poker", "..............................");
        Console.say("SUSIE", Console.p(
                "I climbed a thousand stairs to be told that everyone who went in there "
                        + "is dead?",
                "No thanks. I like being alive. I'm rather good at it."));
        Console.say("HANS", "poker", Console.p(
                "A wise choice.",
                "The labyrinth is not going anywhere. It never does."));

        Console.action("Susie turns around, walks out of the huge door, past the maid, "
                + "down all the stairs, and out of the building.");
        Console.narrate(Console.p(
                "The sun is warm. The city is loud. The labyrinth stays exactly where it is.",
                "Somewhere far away, a map waits for an explorer who never comes."));

        Console.say("MAID", "Leaving already, Madam Susie?");
        Console.say("SUSIE", "happy", Console.p(
                "Yup!",
                "Tell Mister Hans his door is huge."));

        Console.narrate(Console.p(
                "Susie never explored that labyrinth.",
                "She kept exploring other, safer ruins, lived a long and very uneventful "
                        + "life, and died at ninety-six, surrounded by grandchildren who did "
                        + "not believe a word of her stories.",
                "The labyrinth waited. It is still waiting."));

        Console.banner(
                "SECRET ENDING 1",
                "\"The Explorer Who Said No\"",
                "",
                "You refused the call of the labyrinth.");
    }
}
