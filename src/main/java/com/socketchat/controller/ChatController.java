package com.socketchat.controller;

import com.socketchat.message.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class ChatController {

    // Thread-safe list to hold connected users
    private List<String> connectedUsers = new CopyOnWriteArrayList<>();

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();

        // Add user to the list if not already present
        if (username != null && !connectedUsers.contains(username)) {
            connectedUsers.add(username);
            headerAccessor.getSessionAttributes().put("username", username);
        }

        // Create a new ChatMessage for the join event
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setSender(username);
        joinMessage.setUserList(connectedUsers);  // Set the userList

        return joinMessage;
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            connectedUsers.remove(username);

            // Create a new ChatMessage for the leave event
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setSender(username);
            leaveMessage.setUserList(connectedUsers);  // Set the userList

            return leaveMessage;
        }
        return null;
    }
}
