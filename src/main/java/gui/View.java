package gui;

import dataprocess.ResultCalculator;
import logic.InvalidInputDataException;
import logic.Scheduler;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class View extends JFrame {
    private JPanel backGroundPanel;
    private JPanel mainPanel;
    private JPanel setupPanel;
    private JPanel simulationPanel;
    private JLabel timeLimitLabel;
    private JTextField timeLimitTF;
    private JLabel minArrivalTimeLabel;
    private JTextField minArrivalTimeTF;
    private JLabel minServiceTimeLabel;
    private JTextField minServiceTimeTF;
    private JTextField maxArrivalTimeTF;
    private JTextField maxServiceTimeTF;
    private JRadioButton shortestTimeStrategy;
    private JRadioButton shortestQueueStrategy;
    private JLabel strategyLabel;
    private JButton startSimulationButton;
    private JLabel clientNrLabel;
    private JLabel maxArrivalTimeLabel;
    private JLabel maxServiceTimeLabel;
    private JLabel queueNrLabel;
    private JTextField clientNrTF;
    private JTextField queueNrTF;
    private JLabel avgWaitingTimeLabel;
    private JLabel avgServiceTimeLabel;
    private JTextField avgWaitingTimeTF;
    private JTextField avgServiceTimeTF;
    private JLabel peakHourLabel;
    private JTextField peakHourTF;
    private JButton stopSimulationButton;
    private JLabel currentTimeLabel;
    private JTextField currentTimeTF;
    private JLabel generatedClientsLabel;
    private JTextArea generatedClientsTA;
    private JPanel clientQueuesPanel;
    private JPanel clientQueuesPanelActual;
    private JTextArea[] clientQueuesTAArray;

    private int nrOfQueues;

    public View(){
        this.initGUI();
    }

    public void initGUI(){
        this.setTitle("Simulare client - coada");
        this.setSize(1070, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(this.mainPanel);

        this.setVisible(true);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void selectSetupPanel() {
        CardLayout c = (CardLayout)mainPanel.getLayout();
        c.show(mainPanel, "setupPanel");
    }

    public void selectSimulationPanel() {
        CardLayout c = (CardLayout)mainPanel.getLayout();
        c.show(mainPanel, "simulationPanel");
    }

    public Scheduler.SelectionPolicy getSelectedStrategy(){
        if (shortestTimeStrategy.isSelected())
            return Scheduler.SelectionPolicy.SHORTEST_TIME;
        return Scheduler.SelectionPolicy.SHORTEST_QUEUE;
    }

    public int[] getInputValues() {
        int timeLimit, nrOfClients, nrOfQueues, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime;
        int[] inputValues = new int[7];

        try{
            timeLimit = Integer.parseInt(timeLimitTF.getText());
            nrOfClients = Integer.parseInt(clientNrTF.getText());
            nrOfQueues = Integer.parseInt(queueNrTF.getText());
            minArrivalTime = Integer.parseInt(minArrivalTimeTF.getText());
            maxArrivalTime = Integer.parseInt(maxArrivalTimeTF.getText());
            minServiceTime = Integer.parseInt(minServiceTimeTF.getText());
            maxServiceTime = Integer.parseInt(maxServiceTimeTF.getText());

            if (timeLimit <= 0 || nrOfClients <= 0 || nrOfQueues <= 0 || minArrivalTime < 0 || maxArrivalTime < 0 || minServiceTime <= 0 || maxServiceTime <= 0)
                throw new InvalidInputDataException();
            if (minArrivalTime > maxArrivalTime || minServiceTime > maxServiceTime)
                throw new InvalidInputDataException();

            this.nrOfQueues = nrOfQueues;

            inputValues[0] = timeLimit;
            inputValues[1] = nrOfClients;
            inputValues[2] = nrOfQueues;
            inputValues[3] = minArrivalTime;
            inputValues[4] = maxArrivalTime;
            inputValues[5] = minServiceTime;
            inputValues[6] = maxServiceTime;
            return inputValues;
        }
        catch (NumberFormatException | InvalidInputDataException ex){
            showMessage("Date de intrare invalide!");
        }

        return null;
    }

    public void initSimulationFields(){
        if (clientQueuesPanelActual != null)
            clientQueuesPanel.remove(clientQueuesPanelActual);
        clientQueuesPanelActual = new JPanel();
        clientQueuesPanelActual.setLayout(new BoxLayout(clientQueuesPanelActual, BoxLayout.Y_AXIS));
        clientQueuesPanelActual.setBackground(new Color(0xE5, 0xAC, 0x3D));
        clientQueuesPanel.add(clientQueuesPanelActual);

        currentTimeTF.setText("0");
        avgWaitingTimeTF.setText("0");
        avgServiceTimeTF.setText("0");
        peakHourTF.setText("0");
        generatedClientsTA.setText("-");

        Font queueFont = new Font("Courier New", Font.BOLD, 18);
        Font clientFont = new Font("Courier New", Font.PLAIN, 20);

        JPanel[] clientQueuesPanelArray = new JPanel[nrOfQueues];
        JLabel[] clientQueuesLabelArray = new JLabel[nrOfQueues];
        clientQueuesTAArray = new JTextArea[nrOfQueues];
        for (int i = 0; i < nrOfQueues; ++i){
            clientQueuesPanelArray[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
            clientQueuesPanelArray[i].setBackground(new Color(250, 188, 67, 255));

            JLabel queueLabel = new JLabel("Coada " + (i + 1) + ": ");
            queueLabel.setFont(queueFont);
            queueLabel.setForeground(new Color(0x32, 0x25, 0x0D));

            JTextArea queueTA = new JTextArea("inchisa", 0, 55);
            queueTA.setWrapStyleWord(true);
            queueTA.setLineWrap(true);
            queueTA.setEditable(false);
            queueTA.setFont(clientFont);
            queueTA.setForeground(new Color(0x32, 0x25, 0x0D));
            queueTA.setBackground(new Color(0xEB, 0xEF, 0x8D));

            clientQueuesLabelArray[i] = queueLabel;
            clientQueuesTAArray[i] = queueTA;
            clientQueuesPanelArray[i].add(clientQueuesLabelArray[i]);
            clientQueuesPanelArray[i].add(clientQueuesTAArray[i]);

            clientQueuesPanelActual.add(clientQueuesPanelArray[i]);
        }
    }

    public void updateSimulationFields(int currentTime, BlockingQueue<Client> generatedClients, List<ClientQueue> clientQueues, ResultCalculator resultCalculator){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);


        currentTimeTF.setText(String.valueOf(currentTime));
        avgWaitingTimeTF.setText(df.format(resultCalculator.avgWaitingTime));
        avgServiceTimeTF.setText(df.format(resultCalculator.avgServiceTime));
        peakHourTF.setText(df.format(resultCalculator.peakHour));

        String clients = "";
        if (generatedClients.isEmpty())
            clients = "-";
        else {
            for (Client client : generatedClients)
                clients += "(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ";
        }
        generatedClientsTA.setText(clients);

        int i = 0;
        String queueClients;
        for(ClientQueue cq: clientQueues){
            if (cq.getClients().isEmpty())
                queueClients = "inchisa";
            else {
                queueClients = "";
                for (Client client : cq.getClients())
                    queueClients += "(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ";
            }

            clientQueuesTAArray[i].setText(queueClients);
            ++i;
        }
    }

    public void addStartSimulationListener(ActionListener al){
        startSimulationButton.addActionListener(al);
    }

    public void addStopSimulationListener (ActionListener al) {
        stopSimulationButton.addActionListener(al);
    }

    private void createUIComponents() {
        clientQueuesPanel = new JPanel();
        clientQueuesPanel.setLayout(new BoxLayout(clientQueuesPanel, BoxLayout.Y_AXIS));
    }
}
