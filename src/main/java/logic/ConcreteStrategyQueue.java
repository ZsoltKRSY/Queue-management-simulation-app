package logic;

import model.*;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addClient(List<ClientQueue> clientQueues, Client client) {
        ClientQueue minClientQueue = new ClientQueue();
        int minClientNumber = Integer.MAX_VALUE;

        for(ClientQueue cq: clientQueues){
            if (cq.getClients().size() < minClientNumber){
                minClientNumber = cq.getClients().size();
                minClientQueue = cq;
            }
        }

        minClientQueue.addClient(client);
    }
}
