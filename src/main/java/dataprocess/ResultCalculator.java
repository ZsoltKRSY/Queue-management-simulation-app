package dataprocess;

import model.ClientQueue;

import java.util.List;

public class ResultCalculator {
    public float avgWaitingTime;
    public float avgServiceTime;
    public int peakHour;
    private int totalWaitingTime;
    private int totalServiceTime;
    private int peakHourClientNr;

    public ResultCalculator(){
        peakHourClientNr = 0;
        totalWaitingTime = 0;
        avgWaitingTime = 0;
        totalServiceTime = 0;
        avgServiceTime = 0;
        peakHour = 0;
    }

    public void calculateResults (int currentTime, List<ClientQueue> clientQueues){
        int nrTotalClients = 0;
        for(ClientQueue cq: clientQueues) {
            int s = cq.getClients().size();
            nrTotalClients += s;
            if (s > 0) {
                ++totalServiceTime;
                totalWaitingTime += s - 1;
            }
        }

        if (nrTotalClients > peakHourClientNr){
            peakHourClientNr = nrTotalClients;
            peakHour = currentTime;
        }
        if (currentTime != 0) {
            avgWaitingTime = (float)totalWaitingTime / currentTime;
            avgServiceTime = (float)totalServiceTime / currentTime;
        }
    }
}
