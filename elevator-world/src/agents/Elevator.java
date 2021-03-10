package agents;

import platform.Log;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Elevator extends Stepper {
    /**
     *
     */
    private static final long serialVersionUID = -5573356049624076012L;

    public static int T_DOORS = -1;
    public static int T_CLOSE = -1;
    public static int T_TRANSIT = -1;
    public static int T_SLOW = -1;
    public static int T_ACCEL = -1;
    public static int T_FAST = -1;
    public static int CAPACITY = -1;
    public static int NUM_FLOORS = -1;

    private boolean first = false;

    
    
    public static class ElevatorState {
        public enum State {
            IN_TRANSIT,
            DOORS_OPENING,
            DOORS_CLOSING,
            DOORS_OPEN,
            DOORS_CLOSED
        }

        public State state;
        public int number;
        public int currentFloor;
        public int lastFloor;
        public int direction;
        public int time;

        public ElevatorState(int elevatorNumber) {
            state = State.DOORS_CLOSED;
            currentFloor = 1;
            lastFloor = currentFloor;
            direction = 0;
            time = 0;
            number = elevatorNumber;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public int getCurrentFloor() {
            return currentFloor;
        }

        public void setCurrentFloor(int currentFloor) {
            this.currentFloor = currentFloor;
        }

        public int getLastFloor() {
            return lastFloor;
        }

        public void setLastFloor(int lastFloor) {
            this.lastFloor = lastFloor;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "ElevatorState [currentFloor=" + currentFloor + ", direction=" + direction + ", lastFloor="
                    + lastFloor + ", state=" + state + ", time=" + time + "]";
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
    private ElevatorState state;

    enum ActionType {
        ACCEL,
        SLOW,
        FAST,
        OPEN_DOORS,
        CLOSE_DOORS;
    }

    class Action {

        private final ActionType type;
        private final int floor;

        Action(final ActionType type, int floor) {
            this.type = type;
            this.floor = floor;
        }

        Action(final ActionType type) {
            this(type, -1);
        }

        public ActionType getType() {
            return type;
        }

        public int getFloor() {
            return floor;
        }
        
        public String toString() {
            return floor > 0 ? this.type.name() + "_" + floor : this.type.name();
        }

    }

    private List<Pair<Action, Integer>> plan;
    private Pair<Action, Integer> currentAction;
    private Pair<Action, Integer> lastAction;

    public Elevator(int elevatorNumber) {
        super();
        state = new ElevatorState(elevatorNumber);
        plan = new ArrayList<>();
        currentAction = null;
    }

    @Override
    protected void setup() {
        Log.log(this, "Hello!");
    }

    @Override
    protected void takeDown() {
        // Printout a dismissal message
        Log.log(this, "terminating.");
    }

    private void generatePlan() {
        Random r = new Random();
        int currentFloor = state.getCurrentFloor();
        int randomFloor = -1;
        do {
            randomFloor = r.nextInt(NUM_FLOORS) + 1;
        } while (randomFloor == currentFloor);
        Log.log(this, "Current floor: " + currentFloor);
        Log.log(this, "Random floor: " + randomFloor);
        int diff = (int) Math.abs(currentFloor - randomFloor);
        if (diff == 1) {
            Action goToFloor = new Action(ActionType.SLOW, randomFloor);
            plan.add(new Pair<>(goToFloor, Elevator.T_SLOW));
        } else {
            if (randomFloor < currentFloor) {
                Action goToAccel = new Action(ActionType.ACCEL, currentFloor - 1);
                plan.add(new Pair<>(goToAccel, Elevator.T_ACCEL));
                for (int i = currentFloor - 2; i >= randomFloor + 1; i--) {
                    Action goToFast = new Action(ActionType.FAST, i);
                    plan.add(new Pair<>(goToFast, Elevator.T_FAST));
                }
                Action goToSlowDown = new Action(ActionType.ACCEL, randomFloor);
                plan.add(new Pair<>(goToSlowDown, Elevator.T_ACCEL));
            } else {
                Action goToAccel = new Action(ActionType.ACCEL, currentFloor + 1);
                plan.add(new Pair<>(goToAccel, Elevator.T_ACCEL));
                for (int i = currentFloor + 2; i <= randomFloor - 1; i++) {
                    Action goToFast = new Action(ActionType.FAST, i);
                    plan.add(new Pair<>(goToFast, Elevator.T_FAST));
                }
                Action goToSlowDown = new Action(ActionType.ACCEL, randomFloor);
                plan.add(new Pair<>(goToSlowDown, Elevator.T_ACCEL));
            }
        }
        plan.add(new Pair<>(new Action(ActionType.OPEN_DOORS), Elevator.T_DOORS));
        plan.add(new Pair<>(new Action(ActionType.CLOSE_DOORS), Elevator.T_CLOSE));
    }
    
    private void printPlan() {
        for (Pair<Action, Integer> action : plan) {
            //Log.log(this, action.getFst(), action.getSnd());
        }
    }

    private void executePlan() {

        if (currentAction != null) {
            int time = state.getTime();
            if (time > 0) {
                Log.log(this, "executing: ", currentAction.getFst(), state.getTime());
                switch(currentAction.getFst().getType()) {
                    case SLOW:
                    case FAST:
                    case ACCEL:
                    if (first) {
                        state.setLastFloor(state.getCurrentFloor());
                        state.setCurrentFloor(currentAction.getFst().getFloor());
                        int direction = state.getCurrentFloor() > state.getLastFloor() ? 1 : -1;
                        state.setDirection(direction);
                        first = false;
                    }
                    state.setState(ElevatorState.State.IN_TRANSIT);
                    break;
                    case OPEN_DOORS:
                    state.setState(ElevatorState.State.DOORS_OPENING);
                    break;
                    case CLOSE_DOORS:
                    state.setState(ElevatorState.State.DOORS_CLOSING);
                    default:
                    break;
                }
                state.setTime(time - 1);
                return;
            } else {
                lastAction = currentAction;
                currentAction = null;
                if (lastAction != null) {
                    switch (lastAction.getFst().getType()) {
                        case OPEN_DOORS:
                        state.setState(ElevatorState.State.DOORS_OPEN);
                        break;
                        case CLOSE_DOORS:
                        state.setState(ElevatorState.State.DOORS_CLOSED);
                        break;
                        default:     
                        state.setCurrentFloor(lastAction.getFst().getFloor());
                        break;
                    }
                }
            }
        }
       
        if (currentAction == null && !plan.isEmpty()) {
            currentAction = plan.remove(0);
            first = true;
        }

        if (currentAction != null) {
            state.setTime(currentAction.getSnd());
            switch(currentAction.getFst().getType()) {
                case SLOW:
                case FAST:
                case ACCEL:
                break;
                default:
                break;
            }
        }
    }


    @Override
    public Object step() {
        // TODO Auto-generated method stub
        if (plan.isEmpty() && currentAction == null) {
            generatePlan();
            printPlan();
        } else {
            executePlan();
        }
        return state;
    }
}