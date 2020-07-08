package com.company;

import java.net.InetAddress;
import java.util.LinkedList;

public class NodeManager {
    private final LinkedList<Node> connectedNodes = new LinkedList<Node>();

    public void addNewMachine(Node newMachine) {
        connectedNodes.add(newMachine);
    }

    public int machinesOnlineNumber() {
        return connectedNodes.size();
    }

    public Node findByIPAndPort(InetAddress ipToFind, int portToFind){
        for (Node connectedNode : connectedNodes) {
            if (connectedNode.getNodeIPAddress().getHostAddress().equals(ipToFind.getHostAddress())) {
                if (connectedNode.getNodePort() == portToFind) {
                    return connectedNode;
                }
            }
        }
        return null;
    }

    public void nodeLogoff(Node inputNode) {
        connectedNodes.remove(inputNode);
    }
}
