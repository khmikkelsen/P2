package simulator;

import rsa.InvalidRSAKeyException;
import node.database.DatabaseConnection;
import userclient.UserClient;
import node.NodeClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Simulator {

    public static void main(String[] args) throws SQLException, IOException, InvalidRSAKeyException {
        Simulator simulator = new Simulator();
        simulator.start();
    }

    private List<NodeClient> nodes = new ArrayList<>();

    public void start() throws SQLException {
        NodeClient networkNode1 = new NodeClient("Node (1) on network", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_on_network.db"));
        NodeClient networkNode2 = new NodeClient("Node (2) on network", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_2_on_network.db"));
        NodeClient networkNode3 = new NodeClient("Node (3) on network", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_3_on_network.db"));
        NodeClient networkNode4 = new NodeClient("Node (4) on network", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_4_on_network.db"));
        NodeClient networkNode5 = new NodeClient("Node (5) on network", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_5_on_network.db"));
        NodeClient ourNode = new NodeClient("Our node", this, new DatabaseConnection("C://p2_blockchain/sqlite/node_our.db"));

        nodes.addAll(Arrays.asList(
                networkNode1,
                networkNode2,
                networkNode3,
                networkNode4,
                networkNode5,
                ourNode));

        UserClient userClient = new UserClient(this);
    }

    public void broadcastMessage(String messageCommand) {
        for (NodeClient node : nodes) {
            node.getNetworkHandler().sendRequest(messageCommand);
        }
    }

    public void broadcastBlock(NodeClient sendingNode, String blockCommand) {
        System.out.println("Broadcasting blockchain.block from " + sendingNode.getSimulatorName());
        for (NodeClient node : nodes) {
            if (node != sendingNode) {
                node.getNetworkHandler().sendRequest(blockCommand);
            }
        }
    }
}
