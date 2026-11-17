/**
 * Starts the program.
 * A small console menu: play a phase, play both, run the tests, or exit.
 */
public class Main {
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            Console.banner(
                    "LeMon: Labyrinth Explorers Monsters",
                    "Main Menu");

            int choice = Console.choose("What do you want to do?",
                    "Play Phase 1 - The Invitation",
                    "Play Phase 2 - The First Floor",
                    "Play both phases",
                    "Run the tests",
                    "Exit");

            if (choice == 1) {
                PhaseOne.play();
            } else if (choice == 2) {
                PhaseTwo.play();
            } else if (choice == 3) {
                boolean accepted = PhaseOne.play();
                if (accepted) {
                    PhaseTwo.play();
                }
            } else if (choice == 4) {
                Tests.runAll();
                Console.pause();
            } else {
                running = false;
            }
        }

        System.out.println();
        System.out.println("  Thanks for playing!");
        System.out.println();
    }
}
