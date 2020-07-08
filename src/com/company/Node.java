package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    private InetAddress nodeIPAddress;
    private final String nodeName;
    private int nodePort;

    public Node(String name, InetAddress ip, int port) {
        nodeIPAddress = ip;
        nodeName = name;
        nodePort = port;
        System.out.println("New client: name = " + getNodeName() + " ip = " + getNodeIPAddress().getHostAddress() + " port = " + getNodePort());
    }
    public InetAddress getNodeIPAddress(){ return nodeIPAddress; }
    public String getNodeName(){ return nodeName; }
    public int getNodePort(){ return nodePort; }

    public void sendMessageToNode(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, getNodeIPAddress(), getNodePort());
            DatagramSocket socket = new DatagramSocket();
            System.out.println("Message to Node: " + message);
            socket.send(packet);
            socket.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}