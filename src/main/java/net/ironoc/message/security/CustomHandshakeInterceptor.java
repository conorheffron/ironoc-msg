package net.ironoc.message.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger LOGGER = Logger.getLogger(HandshakeInterceptor.class.getName());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // set principal
        Principal principal = request.getPrincipal();
        LOGGER.info(String.format("The user principal for request is %s", principal));
        attributes.put("principal", new UsernamePasswordAuthenticationToken(principal, null));

        if (request instanceof ServletServerHttpRequest) {
            // ser session ID
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        LOGGER.info(String.format("afterHandshake principal details %s", request.getPrincipal()));
    }
}
