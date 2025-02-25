package logic;

import model.*;

import java.util.List;

public interface Strategy {
    void addClient(List<ClientQueue> clientQueues, Client client);
}
