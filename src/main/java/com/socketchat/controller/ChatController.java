package com.socketchat.controller;

// File: ChatController.java

import com.socketchat.message.ChatMessage;
import com.socketchat.message.ReadReceipt;
import com.socketchat.message.TypingStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private List<String> connectedUsers = new CopyOnWriteArrayList<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();
        connectedUsers.add(username);
        headerAccessor.getSessionAttributes().put("username", username);

        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setSender(username);
        joinMessage.setUserList(connectedUsers);

        return joinMessage;
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            connectedUsers.remove(username);

            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setSender(username);
            leaveMessage.setUserList(connectedUsers);

            return leaveMessage;
        }
        return null;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        String recipient = chatMessage.getRecipient();
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", chatMessage);
    }

    @MessageMapping("/chat.typing")
    public void sendTypingNotification(TypingStatus typingStatus) {
        messagingTemplate.convertAndSend("/topic/typing", typingStatus);
    }

    @MessageMapping("/chat.sendReadReceipt")
    public void sendReadReceipt(ReadReceipt readReceipt) {
        readReceipt.setRecipient(readReceipt.getSender());
        messagingTemplate.convertAndSendToUser(readReceipt.getSender(), "/queue/read-receipt", readReceipt);
    }
}
