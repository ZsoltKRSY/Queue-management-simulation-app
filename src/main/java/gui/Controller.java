package gui;

import logic.SimulationManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private final View view;
    private SimulationManager simManager;

    public Controller(View view){
        this.view = view;

        view.addStartSimulationListener(new StartSimulationListener());
        view.addStopSimulationListener(new StopSimulationListener());
    }

    class StartSimulationListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] inputValues = view.getInputValues();

            if (inputValues != null){
                simManager = new SimulationManager(view, inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5], inputValues[6], view.getSelectedStrategy());
                Thread t = new Thread(simManager);
                t.start();
                view.selectSimulationPanel();
            }
        }
    }

    class StopSimulationListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            simManager.stopSimulation();
            view.selectSetupPanel();
        }
    }
}
