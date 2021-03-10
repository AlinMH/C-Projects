package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import platform.Log;
import reader.Scenario;
import utils.Command;
import utils.CommandScheduler;
import utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Simulator extends Agent {
    /**
     *
     */
    private static final long serialVersionUID = -5682305089283386274L;
    public static int TICK_PERIOD;

    private List<Stepper> elevators;
    private List<Stepper> floors;
    private Scenario scenario;
    private float timeScaler;

    public Scenario getScenario() {
        return scenario;
    }

    public List<Stepper> getElevators() {
        return elevators;
    }

    public List<Stepper> getFloors() {
        return floors;
    }


    private void printFloor(Pair<Integer, List<Command>> floor, char[][] matrix) {
        int elevatorWidth = (2 + Elevator.CAPACITY + 2 + 2);

        int offsetHeight = 2 + (floors.size() -  floor.getFst()) * 2 + 1;
        int offsetWidth = 4 + (elevatorWidth + 4) * (elevators.size());

        List<Character> waiting = new ArrayList<>();
        List<Character> notScheduled = new ArrayList<>();

        for (Command c : floor.getSnd()) {

            if (c.getCurrentState() == Command.commandState.WAITING) {
                waiting.add(c.getName());
            } else {
                notScheduled.add(c.getName());
            }
        }

        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                for (int j = 0; j < 14; j++) {
                    if (j != 7)
                        matrix[i + offsetHeight][j + offsetWidth] = '-';
                    else
                        matrix[i + offsetHeight][j + offsetWidth] = '^';
                }
            } else if (i == 0) {
                for (int j = 0; j < Math.min(6, waiting.size()); j++) {
                    matrix[i + offsetHeight][j + offsetWidth] = waiting.get(j);
                }
                for (int j = 0; j < Math.min(6, notScheduled.size()); j++) {
                    matrix[i + offsetHeight][j + offsetWidth + 8] = notScheduled.get(j);
                }
            }
        }

    }

    private void printElevator(Elevator.ElevatorState elevatorState, char[][] matrix) {
        int elevatorWidth = (2 + Elevator.CAPACITY + 2 + 2);
        
        int offsetHeight = 2 + (floors.size() -  elevatorState.getCurrentFloor()) * 2;
        if (elevatorState.state == Elevator.ElevatorState.State.IN_TRANSIT) {
            if (elevatorState.direction == -1) {
                offsetHeight--;
            } else {
                offsetHeight++;
            }
        }

        int offsetWidth = 4 + (elevatorWidth + 4) * (elevatorState.getNumber() - 1);
        
        for (int i = 0; i < elevatorWidth; i++) {
            if (i == 0 | i == 2) {
                for (int j = 0; j < elevatorWidth; j++) {
                    matrix[i + offsetHeight][j + offsetWidth] = '-';
                }
            } else {
                matrix[1 + offsetHeight][offsetWidth] = '|';
                switch (elevatorState.state) {
                    case DOORS_CLOSED:
                    case IN_TRANSIT:
                    matrix[1 + offsetHeight][elevatorWidth + offsetWidth - 1] = '|';
                    break;
                    case DOORS_OPENING:
                    matrix[1 + offsetHeight][elevatorWidth + offsetWidth - 1] = '/';
                    break;
                    case DOORS_CLOSING:
                    matrix[1 + offsetHeight][elevatorWidth + offsetWidth - 1] = '\\';
                    default:
                    break;
                }
                if (elevatorState.getDirection() == 1)
                    matrix[1 + offsetHeight][elevatorWidth + offsetWidth] = '^';
                else {
                    matrix[1 + offsetHeight][elevatorWidth + offsetWidth] = 'V';
                }
            }
        }
    }

    private void printState(List<Elevator.ElevatorState> elevators, List<Pair<Integer, List<Command>>> floors) {
        int width = 4 + (2 + Elevator.CAPACITY + 2 + 2 + 4) * elevators.size() + 14;
        int height = 2 + 1 + 2 * floors.size() + 1;

        int elevatorOffsetHorizontal = (2 + Elevator.CAPACITY + 2 + 2) + 4;
        
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);

        char[][] matrix = new char[height][width];

        
        for (int i = 0; i < height; i++) {
            // prima linie
            if (i == 0) {
                int wallOffset = 4 + (elevatorOffsetHorizontal - 4 ) / 2;
                matrix[i][wallOffset] = '1';
                for (int j = wallOffset + 1; j < width; j++) {
                    if (((j - wallOffset) % elevatorOffsetHorizontal == 0) 
                        && ((j - wallOffset) / elevatorOffsetHorizontal) < elevators.size()) {
                        matrix[i][j] = Character.valueOf((char) ((j) / elevatorOffsetHorizontal + 48 + 1));
                    }
                }
            } else if (i == 1 | i == height - 1) {
                for (int j = 0; j < width; j++) {
                    matrix[i][j] = '=';
                }
            } else if (i % 2 == 1 && i > 2) {
                matrix[i][0] = Character.valueOf((char) ( floors.size() - ((i - 2) / 2) + 48   ));
                matrix[i-1][1] = '|';
                matrix[i][1] = '|';
                matrix[i+1][1] = '|';
            }
        }

        for (Elevator.ElevatorState state : elevators) {
            System.out.println(state + " :: " + state.getNumber());
            printElevator(state, matrix);
        }

        for (Pair<Integer, List<Command>> state : floors) {
            printFloor(state, matrix);
        }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                builder.append(matrix[i][j]);
            }
            builder.append('\n');
        }

        System.out.println(builder.toString());
    }

    public Simulator() {
        super();
    }

    public void mainLoop() {
        List<Elevator.ElevatorState> elevatorStates = new ArrayList<>();
        List<Pair<Integer, List<Command>>> floorStates = new ArrayList<>();
        for (Stepper floor : floors) {
            Pair<Integer, List<Command>> state  = (Pair<Integer, List<Command>>) floor.step();
            floorStates.add(state);
        }

        for (Stepper elevator : elevators) {
            Elevator.ElevatorState state = (Elevator.ElevatorState) elevator.step();
            elevatorStates.add(state);
        }
        printState(elevatorStates, floorStates);

        List<Command> allCommands = new ArrayList<>();
        for (Pair<Integer, List<Command>> pair : floorStates) {
            allCommands.addAll(pair.getSnd());
        }

        Collections.sort(allCommands, new Comparator<Command>() {
            @Override
            public int compare(Command o1, Command o2) {
                return o1.getName() - o2.getName();
            }
        });
        
        int maxHeight = 5;
        int maxWidth = 10;

        StringBuilder[] rows = new StringBuilder[maxHeight];

        for (int i = 0; i < rows.length; i++) {
            rows[i] = new StringBuilder();
        }

        for (int i = 0; i < allCommands.size(); i++)
        {
            if (i >= maxHeight * maxWidth) {
                break;
            }

            Command c = allCommands.get(i);
            StringBuilder commandDescription = new StringBuilder();
            commandDescription.append(c.getName() + " " + c.getFrom() + "->" + c.getTo() + " " + c.getCurrentState().toString());
            rows[i % maxHeight].append(commandDescription.toString() + "   ");
        }

        for (StringBuilder row : rows) {
            System.out.println(row);
        }
    }

    @Override
    protected void setup() {
        Log.log(this, "Hello!");

        Object[] args = getArguments();

        floors = (List<Stepper>) args[0];
        elevators = (List<Stepper>) args[1];
        scenario = (Scenario) args[2];
        timeScaler = (float) args[3];

        TICK_PERIOD = (int) (1000 * timeScaler);

        addBehaviour(new TickerBehaviour(this, TICK_PERIOD) {
            @Override
            protected void onTick() {
                ((Simulator) myAgent).mainLoop();
                System.out.println("[Sim] Current time: " + getTickCount());
            }
        });

        CommandScheduler commandScheduler = new CommandScheduler(scenario.generatorList, this);
        commandScheduler.schedule();

    }
    @Override
    protected void takeDown() {
        // Printout a dismissal message
        Log.log(this, "terminating.");
    }
}