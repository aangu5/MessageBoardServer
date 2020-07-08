package com.company;

import java.io.IOException;
import java.net.*;

public class Manager {
    private final Database database;
    private final int serverPort;
    private final boolean systemOnline;
    private final NodeManager nodeManager = new NodeManager();
    private Node currentNode;
    private Message currentMessage;

    public Manager(Database database, int serverPort) {
        this.database = database;
        this.serverPort = serverPort;
        systemOnline = true;
        try {
            InetAddress serverIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        runSystem();
    }

    private Node createNewNode(InetAddress nodeIP, int nodePort) {
        String nodeName = String.format("Node%s", nodeManager.machinesOnlineNumber() + 1);
        Node newNode = new Node(nodeName, nodeIP, nodePort);
        nodeManager.addNewMachine(newNode);
        return newNode;
    }

    private boolean userRegistration(String inputUsername, String inputPassword) {
        User tempUser = database.getUser(inputUsername);
        if (tempUser == null) {
            database.addUser(inputUsername, inputPassword);
            return true;
        } else {
            return false;
        }
    }

    private User userLogon(String inputUsername) {
        return database.getUser(inputUsername);
    }

    public void runSystem() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(serverPort);
            socket.setSoTimeout(0);
            while (systemOnline) {
                Node currentNode;
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String messages = new String (buffer);
                String[] elements = messages.trim().split(",");
                String command = elements[0].trim();
                System.out.println(messages);
                switch(command) {
                    case "NEW":
                        InetAddress tempNodeIP = InetAddress.getByName(elements[1].trim());
                        int tempNodePort = Integer.parseInt(elements[2].trim());
                        currentNode = createNewNode(tempNodeIP, tempNodePort);
                        currentNode.sendMessageToNode("ACCEPTED");
                        break;
                    case "GETMESSAGES":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        currentNode.sendMessageToNode("MESSAGES," + database.getMessages());
                        break;
                    case "NEWMESSAGE":
                        String messageContent = elements[1].trim();
                        String author = elements[2].trim();
                        Message currentMessage = new Message(database.getNumberOfMessages() + 1, messageContent, author);
                        database.addMessage(currentMessage);
                        break;
                    case "LOGON":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        String inputUsername = elements[3].trim();
                        String inputPassword = elements[4].trim();
                        User currentUser = userLogon(inputUsername);
                        if (currentUser.getPassword().equals(inputPassword)) {
                            currentNode.sendMessageToNode("LOGONTRUE," + currentUser.getUserID() + ","  + inputUsername + "," + inputPassword + "," + currentUser.getMessageCount());
                        } else {
                            currentNode.sendMessageToNode("LOGONFALSE");
                        }
                        break;
                    case "CLEAR":
                        database.clearMessages();
                        break;
                    case "LOGOFF":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        nodeManager.nodeLogoff(currentNode);
                        break;
                    case "NEWUSER":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        inputUsername = elements[3].trim();
                        inputPassword = elements[4].trim();
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        if (userRegistration(inputUsername, inputPassword)) {
                            currentNode.sendMessageToNode("USERCREATED");
                        } else {
                            currentNode.sendMessageToNode("USERNOTCREATED");
                        }
                        break;
                    case "MYMESSAGES":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        inputUsername = elements[3].trim();
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        currentNode.sendMessageToNode("MESSAGES," + database.getUserMessages(inputUsername));
                        break;
                    case "SEARCHMESSAGES":
                        tempNodeIP = InetAddress.getByName(elements[1].trim());
                        tempNodePort = Integer.parseInt(elements[2].trim());
                        String searchTerm = elements[3].trim();
                        currentNode = nodeManager.findByIPAndPort(tempNodeIP, tempNodePort);
                        currentNode.sendMessageToNode("MESSAGES," + database.searchMessages(searchTerm));
                        break;
                    default:
                        System.out.println("I don't understand this message: " + messages);
                        break;
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
