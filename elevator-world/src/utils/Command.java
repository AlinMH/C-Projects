package utils;

public class Command {
    public enum commandState {
        REGISTERING,
        WAITING,
        IN_ELEVATOR,
        COMPLETED;

        public String toString() {
            if (this == REGISTERING) {
                return "R";
            } else if (this == WAITING) {
                return "W";
            } else if (this == IN_ELEVATOR) {
                return "IN";
            } else {
                return "C";
            }
        }
    }

    private commandState currentState = commandState.REGISTERING;
    private int from;
    private int to;
    private char name;

    public Command(int from, int to, char name) {
        this.from = from;
        this.to = to;
        this.name = name;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public commandState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(commandState currentState) {
        this.currentState = currentState;
    }

    @Override
    public String toString() {
        return "Command{" +
                "currentState=" + currentState +
                ", from=" + from +
                ", to=" + to +
                '}';
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }
}
