package com.company;

public class Message {
    private int messageID = 0;
    private String content = "";
    private final String author;
    private final long postedTime;

    public Message(int inputID, String inputContent, String inputAuthor) {
        messageID = inputID;
        content = inputContent;
        author = inputAuthor;
        postedTime = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getMessageID() {
        return messageID;
    }

    public long getPostedTime() {
        return postedTime;
    }

    public String getTable() {
        String table = "messages";
        return table;
    }
}
