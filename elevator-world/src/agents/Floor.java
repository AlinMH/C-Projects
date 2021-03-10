package agents;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import platform.Log;
import utils.Command;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class Floor extends Stepper {

    /**
     *
     */
    private static final long serialVersionUID = 7921881298666997166L;
    private int floorNr;
    private List<Command> commands = new ArrayList<>();

    private static char nameCounter = 'A';

    private static final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

    public int getFloorNr() {
        return floorNr;
    }

    public Floor(int floorNr) {
        super();
        this.floorNr = floorNr;
    }

    @Override
    protected void setup() {
        Log.log(this, "Hello!");

        addBehaviour(new AchieveREResponder(this, template) {
            protected ACLMessage handleRequest(ACLMessage request) {
                //System.out.println("[Floor] Agent " + getLocalName() + ": REQUEST received from " + request.getSender().getName() + ". Command is " + request.getContent());
                //System.out.println("[Floor] Agent " + getLocalName() + ": Agree");
                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            }

            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
                int from = ((Floor) myAgent).getFloorNr();
                int to = Integer.parseInt(request.getContent());
                char name = Floor.nameCounter;
                Floor.nameCounter++;
                Command command = new Command(from, to, name);
                commands.add(command);

                //System.out.println("[Floor] Agent " + getLocalName() + ": Action successfully performed");
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
        });

    }

    @Override
    protected void takeDown() {
        // Printout a dismissal message
        Log.log(this, "terminating.");
    }

    @Override
    public Object step() {
        return new Pair<>(getFloorNr(), commands);
    }
}