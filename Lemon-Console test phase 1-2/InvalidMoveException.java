/** Our own error: thrown when the player picks a move that does not exist. */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
