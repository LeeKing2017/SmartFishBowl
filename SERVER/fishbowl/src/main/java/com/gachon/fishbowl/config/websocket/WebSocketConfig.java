package com.gachon.fishbowl.config.websocket;

import com.gachon.fishbowl.sevice.socket.ArduinoSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final ArduinoSocketService arduinoSocketService;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(arduinoSocketService, "/bowl");
    }
}
