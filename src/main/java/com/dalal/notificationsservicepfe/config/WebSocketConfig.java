package com.dalal.notificationsservicepfe.config;

import com.dalal.notificationsservicepfe.security.AuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// class config en general hiya mowadaf kaysaybo spring kaylancer ga3 lmethods li fih dik sa3a hit rah kat3tbr config dkchi elach machi bdaroura tkon katrj3 bean
// machi fhal component sf kaysayab lobject o kaykhalik hta t3ayat lih nta
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
// 1 - first step in config
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 2 - seconde step for config
    private final AuthChannelInterceptor authChannelInterceptor;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}
