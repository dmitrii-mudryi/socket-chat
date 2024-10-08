package com.socketchat.message;

import java.util.List;

public class ChatMessage {
    private String id;
    private MessageType type;
    private String content;
    private String sender;
    private List<String> userList;
    private String recipient;
    private ReadReceipt readReceipt;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public ReadReceipt getReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(ReadReceipt readReceipt) {
        this.readReceipt = readReceipt;
    }
}
