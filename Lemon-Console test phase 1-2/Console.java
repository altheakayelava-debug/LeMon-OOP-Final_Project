import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Everything the story needs to talk to the player.
 * The screen only PRINTS and READS here: no game rules live in this class,
 * so a window (Swing/JavaFX) could replace it later without touching the story.
 */
public class Console {
    /** How many characters fit on one line before we wrap. */
    private static final int WIDTH = 78;
    private static final int TEXT_WIDTH = 68;      // the 6 spaces of indentation are not counted
    private static final String INDENT = "      ";
    private static final BufferedReader IN =
            new BufferedReader(new InputStreamReader(System.in));
    private static boolean inputEnded = false;

    private Console() {
        // only static helpers, nobody needs to create a Console
    }

    // ---------- reading ----------
    /**
     * Read one line from the keyboard. Returns "" (and remembers it) when the
     * input is over, so the program can never get stuck waiting forever.
     */
    public static String readLine() {
        if (inputEnded) {
            return "";
        }
        try {
            String line = IN.readLine();
            if (line == null) {
                inputEnded = true;
                return "";
            }
            return line.trim();
        } catch (IOException e) {
            inputEnded = true;
            return "";
        }
    }

    /**
     * Show a numbered menu and return the chosen number (1 to options.length).
     * A wrong answer simply shows the menu again.
     */
    public static int choose(String question, String... options) {
        while (true) {
            System.out.println();
            System.out.println("  " + question);
            System.out.println();
            for (int i = 0; i < options.length; i++) {
                System.out.println("    [" + (i + 1) + "] " + options[i]);
            }
            System.out.println();
            System.out.print("  > ");
            String answer = readLine();
            if (inputEnded) {
                return 1;                       // no keyboard left: take the first option
            }
            try {
                int pick = Integer.parseInt(answer);
                if (pick >= 1 && pick <= options.length) {
                    return pick;
                }
            } catch (NumberFormatException e) {
                // not a number: fall through to the message below
            }
            System.out.println("  Please type a number from 1 to " + options.length + ".");
        }
    }

    /** Wait for ENTER, so the player can read at their own speed. */
    public static void pause() {
        System.out.println();
        System.out.print("  (press ENTER to continue)");
        readLine();
        System.out.println();
    }

    // ---------- printing ----------
    /** A full-width line made of the given character. */
    public static void rule(char character) {
        System.out.print("  ");
        for (int i = 0; i < WIDTH - 2; i++) {
            System.out.print(character);
        }
        System.out.println();
    }

    /** A boxed title, used for the game's name, a phase, or an ending. */
    public static void banner(String... lines) {
        System.out.println();
        rule('=');
        for (String line : lines) {
            if (line.isEmpty()) {
                System.out.println();
            } else {
                System.out.println("   " + line);
            }
        }
        rule('=');
        System.out.println();
    }

    /** The name of the place where the next scene happens. */
    public static void scene(String place) {
        System.out.println();
        rule('-');
        System.out.println("  " + place);
        rule('-');
    }

    /** Something that happens, not something someone says. */
    public static void narrate(String text) {
        System.out.println();
        printParagraphs(text);
    }

    /** A stage direction, like *Susie hands the letter to the guard. */
    public static void action(String text) {
        System.out.println();
        ArrayList<String> rows = wrap(text, TEXT_WIDTH - 2);
        for (int i = 0; i < rows.size(); i++) {
            System.out.println(INDENT + (i == 0 ? "* " : "  ") + rows.get(i));
        }
    }

    /**
     * Somebody speaks.
     * speaker: "SUSIE", "HANS", "GUARD", "MAID"
     * emotion: "happy", "poker", "shocked"... or null when there is none
     */
    public static void say(String speaker, String emotion, String text) {
        System.out.println();
        System.out.println("  " + speaker + (emotion == null || emotion.isEmpty() ? "" : " (" + emotion + ")"));
        printParagraphs(text);
    }

    /** Somebody speaks, without an emotion. */
    public static void say(String speaker, String text) {
        say(speaker, null, text);
    }

    /** One line of game information: a battle message, an item, a checkpoint. */
    public static void message(String text) {
        for (String row : wrap(text, TEXT_WIDTH)) {
            System.out.println(INDENT + row);
        }
    }

    /** A plain screen line, used for the battle header and the HP bars. */
    public static void write(String text) {
        System.out.println("  " + text);
    }

    // ---------- the small helpers behind the printing ----------
    /** A string built from paragraphs: p("one", "two") becomes "one\n\ntwo". */
    public static String p(String... paragraphs) {
        String text = "";
        for (int i = 0; i < paragraphs.length; i++) {
            if (i > 0) {
                text += "\n\n";
            }
            text += paragraphs[i];
        }
        return text;
    }

    private static void printParagraphs(String text) {
        String[] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                System.out.println();
                continue;
            }
            for (String row : wrap(paragraph, TEXT_WIDTH)) {
                System.out.println(INDENT + row);
            }
        }
    }

    /** Break a paragraph into rows that fit, without cutting words. */
    private static ArrayList<String> wrap(String text, int width) {
        ArrayList<String> rows = new ArrayList<>();
        String[] words = text.split(" ");
        String row = "";
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (row.isEmpty()) {
                row = word;
            } else if (row.length() + 1 + word.length() <= width) {
                row += " " + word;
            } else {
                rows.add(row);
                row = word;
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return rows;
    }
}
