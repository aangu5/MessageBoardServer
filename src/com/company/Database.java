package com.company;

import java.sql.*;

public class Database {
    private Connection connection = null;
    private final Statement statement = null;
    private final PreparedStatement preparedStatement = null;

    public Database(String inputJDBC, String inputUsername, String inputPassword) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(inputJDBC,inputUsername, inputPassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public void functionToExecute(String inputSQL) {
        PreparedStatement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(inputSQL);
            stmt.execute();
            stmt.close();
            connection.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Executed correctly!");
    }

    public void addMessage(Message inputMessage) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO MESSAGES VALUES (?, ?, ?, ?);");
            statement.setInt(1, inputMessage.getMessageID());
            statement.setString(2, inputMessage.getAuthor());
            statement.setString(3, inputMessage.getContent());
            statement.setLong(4, inputMessage.getPostedTime());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void addUser(String inputUsername, String inputPassword) {
        User tempUser = new User(getNumberOfUsers() + 1, inputUsername, inputPassword, 0);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?, ?);");
            statement.setInt(1, tempUser.getUserID());
            statement.setString(2, tempUser.getName());
            statement.setString(3, tempUser.getPassword());
            statement.setInt(4, tempUser.getMessageCount());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public User getUser(String inputUsername) {
        Statement stmt;
        User outputUser = null;
        try {
            connection.setAutoCommit(false);

            stmt = connection.createStatement();
            String inputSQL = "SELECT * FROM users WHERE username='" + inputUsername + "';";

            ResultSet rs = stmt.executeQuery(inputSQL);
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int messageCount = rs.getInt("messageCount");
                outputUser = new User(userID, username, password, messageCount);
            }
            stmt.close();
            connection.commit();
            System.out.println("Records accessed successfully");
            return outputUser;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public String getMessages() {
        Statement stmt;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            String inputSQL = "SELECT id,author, content FROM messages;";
            return getMessages(stmt, inputSQL);
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public String getUserMessages(String inputUsername) {
        Statement stmt;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            String inputSQL = "SELECT id,author, content FROM messages WHERE author='" + inputUsername + "';";
            return getMessages(stmt, inputSQL);
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public String searchMessages(String searchTerm) {
        Statement stmt;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            String inputSQL = "SELECT id,author, content FROM messages WHERE content LIKE '%" + searchTerm + "%';SELECT id,author, content FROM messages WHERE author LIKE '%" + searchTerm + "%';";
            return getMessages(stmt, inputSQL);
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    private String getMessages(Statement stmt, String inputSQL) throws SQLException {
        StringBuilder output;
        ResultSet rs;

        output = new StringBuilder();
        int count = 0;
        boolean isResultSet = stmt.execute(inputSQL);
        boolean hasResults = stmt.execute(inputSQL);
        while (hasResults) {
            if (isResultSet) {
                rs = stmt.getResultSet();
                while ( rs.next() ) {
                    output.append(rs.getInt("id")).append("|");
                    output.append(rs.getString("author")).append("|");
                    output.append(rs.getString("content")).append(";");
                }
                rs.close();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    break;
                }
                System.out.printf("Result %d is just a count: %d", count, stmt.getUpdateCount());
            }
            count++;
            isResultSet = stmt.getMoreResults();
            hasResults = stmt.getMoreResults();
        }
        stmt.close();
        connection.commit();
        System.out.println("Records accessed successfully");
        return output.toString();
    }

    public int getNumberOfMessages() {
        String inputSQL = "SELECT COUNT(*) FROM messages;";
        return getNumberOf(inputSQL);
    }

    public int getNumberOfUsers() {
        String inputSQL = "SELECT COUNT(*) FROM users;";
        return getNumberOf(inputSQL);
    }

    private int getNumberOf(String inputSQL) {
        Statement stmt;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(inputSQL);
            resultSet.next();
            int number = resultSet.getInt("count");
            stmt.close();
            connection.commit();
            return number;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            return 0;
        }
    }

    public void clearMessages() {
        String inputSQL = "DELETE FROM messages;";
        Statement stmt;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute(inputSQL);
            stmt.close();
            connection.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }
    }

}


