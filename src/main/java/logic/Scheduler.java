package logic;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private final List<ClientQueue> clientQueues;
    private final int maxNrQueues;
    private Strategy strategy;

    public enum SelectionPolicy{
        SHORTEST_QUEUE, SHORTEST_TIME
    }

    public Scheduler(int maxNrQueues, Scheduler.SelectionPolicy selectionPolicy){
        this.maxNrQueues = maxNrQueues;

        clientQueues = new ArrayList<>(maxNrQueues);
        generateClientQueues();
        setStrategy(selectionPolicy);
    }

    private void generateClientQueues(){
        for(int i = 0; i < maxNrQueues; ++i){
            ClientQueue cq = new ClientQueue();
            clientQueues.add(cq);

            Thread t = new Thread(cq);
            t.start();
        }
    }

    public List<ClientQueue> getQueues(){
        return clientQueues;
    }

    public void setStrategy(SelectionPolicy policy){
        if (policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ConcreteStrategyQueue();
        else if (policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new ConcreteStrategyTime();
        else
            System.exit(-1);
    }

    public void dispatchClient(Client client){
        strategy.addClient(clientQueues, client);
    }
}
