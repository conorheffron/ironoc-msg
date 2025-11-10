package net.ironoc.message.controller;

import net.ironoc.message.domain.Greeting;
import net.ironoc.message.domain.HelloMessage;
import net.ironoc.message.domain.UserMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> activeSessionsByUserId = new ConcurrentHashMap<>();

    public MessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/broadcast/user")
    public Greeting greeting(HelloMessage message, @Header("simpSessionId") String sessionId) {
        activeSessionsByUserId.put(message.getName(), sessionId);
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/send-to-user") // Maps to /app/send
    public void sendMessageToUser(@Payload UserMessage userMessage,
                                  Principal principal,
                                  @Header("simpSessionId") String sessionId,
                                  Message<Object> message) {
        String from  = principal.getName();// logged in user
        String recipient = userMessage.getTargetName();
        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", userMessage.getContent());
    }
}
