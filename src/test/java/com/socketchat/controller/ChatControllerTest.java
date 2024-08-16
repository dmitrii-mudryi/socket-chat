package com.socketchat.controller;

import com.socketchat.message.ChatMessage;
import com.socketchat.message.ReadReceipt;
import com.socketchat.message.TypingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    private ChatController chatController;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        chatController = new ChatController(messagingTemplate);
    }

    @Test
    void sendMessageReturnsChatMessageWithId() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent("Hello");

        ChatMessage result = chatController.sendMessage(chatMessage);

        assertNotNull(result.getId());
        assertEquals("Hello", result.getContent());
    }

    @Test
    void addUserAddsUserToConnectedUsers() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("user1");
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes); // Ensure session attributes are not null

        ChatMessage result = chatController.addUser(chatMessage, headerAccessor);

        assertTrue(result.getUserList().contains("user1"));
    }

    @Test
    void removeUserRemovesUserFromConnectedUsers() {
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("username", "user1");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("user1");
        chatController.addUser(chatMessage, headerAccessor);
        ChatMessage result = chatController.removeUser(headerAccessor);

        assertFalse(result.getUserList().contains("user1"));
    }

    @Test
    void sendPrivateMessageSendsMessageToRecipient() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRecipient("user2");

        chatController.sendPrivateMessage(chatMessage);

        verify(messagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/private"), any(ChatMessage.class));
    }

    @Test
    void sendTypingNotificationSendsTypingStatus() {
        TypingStatus typingStatus = new TypingStatus();
        typingStatus.setUsername("user1");
        typingStatus.setTyping(true);

        chatController.sendTypingNotification(typingStatus);

        verify(messagingTemplate).convertAndSend(eq("/topic/typing"), any(TypingStatus.class));
    }

    @Test
    void sendReadReceiptSendsReadReceiptToSender() {
        ReadReceipt readReceipt = new ReadReceipt();
        readReceipt.setSender("user1");
        readReceipt.setMessageId(UUID.randomUUID().toString());

        chatController.sendReadReceipt(readReceipt);

        verify(messagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/read-receipt"), any(ReadReceipt.class));
    }
}