package com.company;

public class Main {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("That's not the right number of arguments!");
            System.exit(0);
        }
        try {
            int inputPort = Integer.parseInt(args[0]);
            String jdbc = args[1];
            String username = args[2];
            String password = args[3];
            System.out.println("Server running on port " + inputPort);
            Database database = new Database(jdbc, username, password);
            System.out.println("There are currently " + database.getNumberOfMessages() + " messages...");
            Manager manager = new Manager(database, inputPort);
            System.out.println(database.getMessages());
        } catch (NumberFormatException e) {
         e.printStackTrace();
        }
    }
}
