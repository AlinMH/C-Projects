package agents.behaviors;

import agents.Floor;
import agents.Simulator;
import agents.Stepper;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import reader.Generator;

import java.util.List;
import java.util.Random;

public class CommandSenderBehavior extends TickerBehaviour {
    private Generator generator;
    private int maxTicks;

    public CommandSenderBehavior(Agent a, Generator generator, int maxTicks) {
        super(a, generator.period * Simulator.TICK_PERIOD);
        this.generator = generator;
        this.maxTicks = maxTicks;
    }

    @Override
    protected void onTick() {
        List<Stepper> floors = ((Simulator) myAgent).getFloors();
        Random random = new Random();

        int max = Math.toIntExact(generator.oTo);
        int min = Math.toIntExact(generator.oFrom);
        int randomFloorIdx = random.nextInt((max - min) + 1) + min - 1;
        Floor sourceFloor = (Floor) floors.get(randomFloorIdx);

        int sourceFloorNr = sourceFloor.getFloorNr();
        System.out.println("[CS] Chosen floor is " + sourceFloorNr);

        int destFloorNr;
        do {
            max = Math.toIntExact(generator.dTo);
            min = Math.toIntExact(generator.dFrom);
            destFloorNr = random.nextInt((max - min) + 1) + min;
        } while (destFloorNr == sourceFloorNr);

        System.out.println("[CS] Destination floor is " + destFloorNr);
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(sourceFloor.getAID());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setContent(String.valueOf(destFloorNr));

        myAgent.addBehaviour(new AchieveREInitiator(myAgent, msg) {
            protected void handleInform(ACLMessage inform) {
                System.out.println("[Sim] Agent " + inform.getSender().getName() + " successfully performed the requested action");
            }

            protected void handleRefuse(ACLMessage refuse) {
                System.out.println("[Sim] Agent " + refuse.getSender().getName() + " refused to perform the requested action");
            }
        });

        int tickCount = getTickCount();
        System.out.println("<==== Tick " + tickCount + " ====>");

        if (tickCount >= maxTicks) {
            stop();
        }
    }
}
