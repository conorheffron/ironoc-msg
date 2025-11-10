package net.ironoc.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.message.domain.BroadcastUserJoinedResp;
import net.ironoc.message.domain.BroadcastUserJoinedReq;
import net.ironoc.message.domain.UserMessageResp;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.logging.Logger;

@RestController
public class MessageController {

    private static final Logger LOGGER = Logger.getLogger(MessageController.class.getName());

    private final SimpMessagingTemplate messagingTemplate;

    private final  ObjectMapper mapper = new ObjectMapper();

    public MessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/broadcast")
    @SendTo("/topic/broadcast/user")
    public BroadcastUserJoinedResp broadcastUserJoined(@Payload BroadcastUserJoinedReq broadcastUserJoinedReq,
                                                       @Header("simpSessionId") String sessionId) {
        String userId = broadcastUserJoinedReq.getName();
        LOGGER.info(String.format("Broadcasting message for session: %s & user: %s", sessionId, userId));
        return new BroadcastUserJoinedResp(HtmlUtils.htmlEscape(userId) + " joined the chat!");
    }

    @MessageMapping("/send-to-user")
    public void sendMessageToUser(@Payload UserMessageResp userMessageResp,
                                  Principal principal,
                                  @Header("simpSessionId") String sessionId,
                                  Message<Object> message) {
        String from  = principal.getName();// logged in user
        userMessageResp.setSender(from);
        userMessageResp.setBootMessage(message);
        userMessageResp.setSenderSessionId(sessionId);

        LOGGER.info(String.format("Send message to user, userMessage=%s", userMessageResp));
        String recipient = userMessageResp.getTargetName();

        // Convert object to JSON string
        String userMsgJson = null;
        try {
            userMsgJson = mapper.writeValueAsString(userMessageResp);
        } catch (JsonProcessingException e) {
           LOGGER.severe(String.format("Error serializing muser message to JSON, userMsg=%s", userMessageResp));
        }

        messagingTemplate.convertAndSendToUser(from, "/queue/messages", userMsgJson);// to sender queue
        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", userMsgJson);// to target recipient
    }
}
