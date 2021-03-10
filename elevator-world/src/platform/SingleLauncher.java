package platform;

import agents.Elevator;
import agents.Floor;
import agents.Simulator;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import reader.ConfigReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Launches both containers and associated agents.
 *
 * @author Andrei Olaru
 */
public class SingleLauncher {
    static String configFilePath;
    static String scenarioName;
    static float timeScaler;

    AgentContainer container;


    void setupPlatform() {
        Properties mainProps = new ExtendedProperties();
        mainProps.setProperty(Profile.GUI, "true"); // start the JADE GUI
        mainProps.setProperty(Profile.LOCAL_HOST, "localhost");
        mainProps.setProperty(Profile.LOCAL_PORT, "1202");
        mainProps.setProperty(Profile.CONTAINER_NAME, "Proiect"); // you can rename it
        mainProps.setProperty(Profile.PLATFORM_ID, "ami-agents");

        ProfileImpl profile = new ProfileImpl(mainProps);

        container = Runtime.instance().createMainContainer(profile);
    }

    /**
     * Starts the agents assigned to the main container.
     */
    void startAgents() {

        List<Agent> floors = new ArrayList<>();
        List<Agent> elevators = new ArrayList<>();
        ConfigReader configReader = new ConfigReader(configFilePath);
        configReader.parseConfigFile(scenarioName);

        int numFloors = Math.toIntExact(configReader.numFloors);

        Elevator.T_DOORS = Math.toIntExact(configReader.tDoors);
        Elevator.T_CLOSE = Math.toIntExact(configReader.tClose);
        Elevator.T_TRANSIT = Math.toIntExact(configReader.tTransit);
        Elevator.T_SLOW = Math.toIntExact(configReader.tSlow);
        Elevator.T_ACCEL = Math.toIntExact(configReader.tAccel);
        Elevator.T_FAST = Math.toIntExact(configReader.tFast);
        Elevator.CAPACITY = Math.toIntExact(configReader.capacity);
        Elevator.NUM_FLOORS = numFloors;

        for (int i = 0; i < numFloors; i++) {
            try {
                Agent floor = new Floor(i + 1);
                floors.add(floor);
                AgentController floorAgentController = container.acceptNewAgent("floor" + (i + 1), floor);
                floorAgentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        int numElevators = Math.toIntExact(configReader.numElevators);
        for (int i = 0; i < numElevators; i++) {
            try {
                Agent elevator = new Elevator(i + 1);
                elevators.add(elevator);
                AgentController elevatorAgentController = container.acceptNewAgent("elevator" + (i + 1), elevator);
                elevatorAgentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        try {
            Object[] args = new Object[4];
            args[0] = floors;
            args[1] = elevators;
            args[2] = configReader.scenario;
            args[3] = timeScaler;

            AgentController simulatorAgentController = container.createNewAgent("simulator", Simulator.class.getName(), args);
            simulatorAgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Invalid number of arguments! Expected 3, got" + args.length);
            return;
        }

        SingleLauncher launcher = new SingleLauncher();
        configFilePath = args[0];
        scenarioName = args[1];
        timeScaler = Float.parseFloat(args[2]);

        launcher.setupPlatform();
        launcher.startAgents();
    }

}
