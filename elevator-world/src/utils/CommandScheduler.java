package utils;

import agents.Simulator;
import agents.behaviors.CommandSenderBehavior;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import reader.Generator;

import java.util.List;

public class CommandScheduler {
    private List<Generator> generatorList;
    private Agent simulator;

    public CommandScheduler(List<Generator> generatorList, Agent simulator) {
        this.generatorList = generatorList;
        this.simulator = simulator;
    }

    public void schedule() {
        for (Generator generator: generatorList) {
            int start = Math.toIntExact(generator.tFrom);
            int end = Math.toIntExact(generator.tTo);
            int period = Math.toIntExact(generator.period);
            int maxTicks = (end - start + 1) / period;

            if (start - 1 == 0) {
                simulator.addBehaviour(new OneShotBehaviour(simulator) {
                    @Override
                    public void action() {
                        myAgent.addBehaviour(new CommandSenderBehavior(myAgent, generator, maxTicks));
                    }
                });
            } else {
                int delay = (start - 1) * Simulator.TICK_PERIOD;
                simulator.addBehaviour(new WakerBehaviour(simulator, delay) {
                    @Override
                    protected void onWake() {
                        super.onWake();
                        myAgent.addBehaviour(new CommandSenderBehavior(myAgent, generator, maxTicks));
                    }
                });
            }

        }
    }
}
