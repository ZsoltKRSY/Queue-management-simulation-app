package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientQueue implements Runnable {
    private volatile boolean queueUp;
    private final BlockingQueue<Client> clients;
    private final AtomicInteger waitingPeriod;

    public ClientQueue(){
        queueUp = true;
        clients = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addClient(Client client){
        clients.add(client);
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    public List<Client> getClients(){
        return new ArrayList<>(clients);
    }

    public int getWaitingPeriod(){
        return waitingPeriod.get();
    }

    public void stopQueue(){
        queueUp = false;
    }

    @Override
    public void run() {
        while (queueUp){
            try {
                Client currentClient = clients.peek();
                if (currentClient != null) {
                    Thread.sleep(1000L);
                    waitingPeriod.decrementAndGet();
                    currentClient.decrementServiceTime();

                    if (currentClient.getServiceTime() <= 0)
                        clients.remove(currentClient);
                }
            }
            catch(InterruptedException ex){}

        }
    }
}
