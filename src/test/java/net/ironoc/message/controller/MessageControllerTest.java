package net.ironoc.message.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.ironoc.message.domain.BroadcastUserJoinedReq;
import net.ironoc.message.domain.BroadcastUserJoinedResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.HtmlUtils;

class MessageControllerTest {

    private MessageController controller; // Replace with your actual class name

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SimpMessagingTemplate simpMessagingTemplateMock =  Mockito.mock(SimpMessagingTemplate.class);
        controller = new MessageController(simpMessagingTemplateMock); // No dependencies in this method
    }

    @Test
    void testBroadcastUserJoined_NormalName() {
        // Arrange
        BroadcastUserJoinedReq req = new BroadcastUserJoinedReq();
        req.setName("Alice");
        String sessionId = "session123";

        // Act
        BroadcastUserJoinedResp resp = controller.broadcastUserJoined(req, sessionId);

        // Assert
        assertEquals("Alice joined the chat!", resp.getContent());
    }

    @Test
    void testBroadcastUserJoined_HtmlEscaping() {
        // Arrange
        BroadcastUserJoinedReq req = new BroadcastUserJoinedReq();
        req.setName("<script>alert('x')</script>");
        String sessionId = "session456";

        // Act
        BroadcastUserJoinedResp resp = controller.broadcastUserJoined(req, sessionId);

        // Assert — HTML should be escaped
        assertEquals(HtmlUtils.htmlEscape("<script>alert('x')</script>") + " joined the chat!", resp.getContent());
    }

    @Test
    void testBroadcastUserJoined_EmptyName() {
        // Arrange
        BroadcastUserJoinedReq req = new BroadcastUserJoinedReq();
        req.setName("");
        String sessionId = "session789";

        // Act
        BroadcastUserJoinedResp resp = controller.broadcastUserJoined(req, sessionId);

        // Assert
        assertEquals(" joined the chat!", resp.getContent());
    }
}
