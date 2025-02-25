package logic;

import model.*;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addClient(List<ClientQueue> clientQueues, Client client) {
        ClientQueue minClientQueue = new ClientQueue();
        int minWaitingPeriod = Integer.MAX_VALUE;

        for(ClientQueue cq: clientQueues){
            if (cq.getWaitingPeriod() < minWaitingPeriod){
                minWaitingPeriod = cq.getWaitingPeriod();
                minClientQueue = cq;
            }
        }

        minClientQueue.addClient(client);
    }
}
