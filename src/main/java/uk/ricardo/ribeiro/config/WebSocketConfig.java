package uk.ricardo.ribeiro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import uk.ricardo.ribeiro.WebSocketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricardoribeiro on 23/02/2018.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean("sessions")
    public List<WebSocketSession> getSessions() {
        return new ArrayList<>();
    }

    @Value("$(parser.ms.url)")
    private String parserUrl;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(getSessions()), "/metrics");
    }
}