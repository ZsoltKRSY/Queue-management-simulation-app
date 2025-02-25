package logic;

import dataprocess.EventLogger;
import dataprocess.ResultCalculator;
import model.*;
import gui.View;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimulationManager implements Runnable {
    private volatile boolean simulationUp;
    private final View view;
    private final int timeLimit;
    private final int minArrivalTime, maxArrivalTime;
    private final int minServiceTime, maxServiceTime;
    private final int nrOfClients;
    public final Scheduler scheduler;
    private final BlockingQueue<Client> generatedClients;

    public SimulationManager(View view, int timeLimit, int nrOfClients, int nrOfQueues, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, Scheduler.SelectionPolicy selectionPolicy) {
        simulationUp = true;
        this.view = view;
        this.timeLimit = timeLimit;
        this.nrOfClients = nrOfClients;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;

        scheduler = new Scheduler(nrOfQueues, selectionPolicy);
        generatedClients = new LinkedBlockingQueue<>(nrOfClients);
        generateRandomClients();
    }

    private void generateRandomClients() {
        for(int i = 0; i < nrOfClients; ++i){
            int arrTime = (int)(Math.random() * (maxArrivalTime - minArrivalTime) + minArrivalTime);
            int servTime = (int)(Math.random() * (maxServiceTime - minServiceTime) + minServiceTime);
            generatedClients.add(new Client(i + 1, arrTime, servTime));
        }
    }

    public void stopSimulation(){
        for (ClientQueue cq: scheduler.getQueues())
            cq.stopQueue();
        simulationUp = false;
    }

    public synchronized boolean workOnData(int currentTime, EventLogger eventLogger, ResultCalculator resultCalculator){
        resultCalculator.calculateResults(currentTime, scheduler.getQueues());
        eventLogger.writeEventLog(currentTime, generatedClients, scheduler.getQueues());
        view.updateSimulationFields(currentTime, generatedClients, scheduler.getQueues(), resultCalculator);

        boolean existsClient = !generatedClients.isEmpty();
        for (ClientQueue cq: scheduler.getQueues()){
            if (!cq.getClients().isEmpty()){
                existsClient = true;
                break;
            }
        }
        return existsClient;
    }

    @Override
    public void run() {
        int currentTime = 0;
        boolean existsClient = true;
        EventLogger eventLogger = new EventLogger();
        ResultCalculator resultCalculator = new ResultCalculator();
        view.initSimulationFields();

        while (simulationUp && existsClient && currentTime <= timeLimit){
            try {
                for (Client client : generatedClients) {
                    if (client.getArrivalTime() == currentTime) {
                        scheduler.dispatchClient(client);
                        generatedClients.remove(client);
                    }
                }

                existsClient = this.workOnData(currentTime, eventLogger, resultCalculator);
                Thread.sleep(1000L);

                ++currentTime;
            }
            catch (InterruptedException ex){}
        }

        eventLogger.terminateLogFileWriter(resultCalculator);
    }
}
