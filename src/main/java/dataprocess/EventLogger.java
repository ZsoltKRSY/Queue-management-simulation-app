package dataprocess;

import model.Client;
import model.ClientQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class EventLogger {
    private FileWriter logFileWrinter;

    public EventLogger() {
        int index = -1;
        Scanner indexFileScanner;
        FileWriter indexFileWriter;

        try {
            indexFileScanner = new Scanner(new File("index.txt"));
            index = Integer.parseInt(indexFileScanner.nextLine());

            indexFileScanner.close();
        } catch (FileNotFoundException e) {
            System.exit(-1);
        }

        try {
            indexFileWriter = new FileWriter("index.txt");
            indexFileWriter.write(Integer.valueOf(index + 1).toString());

            indexFileWriter.close();

        } catch (IOException e) {
            System.exit(-1);
        }

        File file = new File("log" + index + ".txt");
        try {
            if (!file.createNewFile())
                throw new IOException();
        } catch (IOException e) {
            System.exit(-1);
        }
        try {
            logFileWrinter = new FileWriter(file.getName());
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public void writeEventLog(int currentTime, BlockingQueue<Client> generatedClients, List<ClientQueue> clientQueues){
        try {
            logFileWrinter.write("Timp " + currentTime + "\n" + "Clientii in asteptare: ");
            for(Client client: generatedClients)
                logFileWrinter.write("(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ");
            int i = 1;
            for(ClientQueue cq: clientQueues){
                logFileWrinter.write("\nCoada " + i + ": ");
                if (cq.getClients().isEmpty())
                    logFileWrinter.write("inchisa");
                else {
                    for (Client client : cq.getClients())
                        logFileWrinter.write("(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ");
                }
                ++i;
            }
            logFileWrinter.write("\n\n");

        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public void terminateLogFileWriter(ResultCalculator resultCalculator){
        try {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            logFileWrinter.write("Timpul mediu de asteptare: " + df.format(resultCalculator.avgWaitingTime) + "\n" +
                    "Timpul mediu de service: " + df.format(resultCalculator.avgServiceTime) + "\n" +
                    "Ora de varf: " + resultCalculator.peakHour);
            logFileWrinter.close();
        } catch (IOException e) {
            System.exit(-2);
        }
    }
}
