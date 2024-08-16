package com.socketchat.controller;

import com.socketchat.message.ChatMessage;
import com.socketchat.message.ReadReceipt;
import com.socketchat.message.TypingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final List<String> connectedUsers = new CopyOnWriteArrayList<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        try {
            if (chatMessage == null) {
                throw new IllegalArgumentException("ChatMessage cannot be null");
            }
            chatMessage.setId(UUID.randomUUID().toString());
            return chatMessage;
        } catch (Exception e) {
            logger.error("Error sending message", e);
            return null;
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (chatMessage == null || chatMessage.getSender() == null) {
                throw new IllegalArgumentException("ChatMessage or sender cannot be null");
            }
            String username = chatMessage.getSender();
            connectedUsers.add(username);
            headerAccessor.getSessionAttributes().put("username", username);

            ChatMessage joinMessage = new ChatMessage();
            joinMessage.setType(ChatMessage.MessageType.JOIN);
            joinMessage.setSender(username);
            joinMessage.setUserList(connectedUsers);

            return joinMessage;
        } catch (Exception e) {
            logger.error("Error adding user", e);
            return null;
        }
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(SimpMessageHeaderAccessor headerAccessor) {
        try {
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
        } catch (Exception e) {
            logger.error("Error removing user", e);
            return null;
        }
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        try {
            if (chatMessage == null || chatMessage.getRecipient() == null) {
                throw new IllegalArgumentException("ChatMessage or recipient cannot be null");
            }
            String recipient = chatMessage.getRecipient();
            messagingTemplate.convertAndSendToUser(recipient, "/queue/private", chatMessage);
        } catch (Exception e) {
            logger.error("Error sending private message", e);
        }
    }

    @MessageMapping("/chat.typing")
    public void sendTypingNotification(TypingStatus typingStatus) {
        try {
            if (typingStatus == null) {
                throw new IllegalArgumentException("TypingStatus cannot be null");
            }
            messagingTemplate.convertAndSend("/topic/typing", typingStatus);
        } catch (Exception e) {
            logger.error("Error sending typing notification", e);
        }
    }

    @MessageMapping("/chat.sendReadReceipt")
    public void sendReadReceipt(ReadReceipt readReceipt) {
        try {
            if (readReceipt == null || readReceipt.getSender() == null) {
                throw new IllegalArgumentException("ReadReceipt or sender cannot be null");
            }
            readReceipt.setRecipient(readReceipt.getSender());
            messagingTemplate.convertAndSendToUser(readReceipt.getSender(), "/queue/read-receipt", readReceipt);
        } catch (Exception e) {
            logger.error("Error sending read receipt", e);
        }


    }
}