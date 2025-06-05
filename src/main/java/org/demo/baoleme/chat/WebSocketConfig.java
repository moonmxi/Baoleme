package org.demo.baoleme.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 * <p>
 * 该类用于配置 WebSocket 相关设置，包括注册 WebSocket 处理器和跨域配置。
 * 通过 {@code @EnableWebSocket} 启用 Spring 的 WebSocket 支持。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * WebSocket 消息处理器实例
     * <p>
     * 用于处理 WebSocket 连接的生命周期事件和消息交互。
     */
    private final ChatWebSocketHandler chatWebSocketHandler;

    /**
     * 构造方法注入依赖
     *
     * @param chatWebSocketHandler 自动注入的 WebSocket 处理器 bean
     */
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    /**
     * 注册 WebSocket 处理器并配置访问规则
     *
     * @param registry WebSocket 处理器注册中心，用于管理 WebSocket 端点
     * <p>
     * 配置说明:
     * 1. 将 {@code chatWebSocketHandler} 注册到路径 {@code "/ws/chat"}
     * 2. 设置允许所有来源跨域访问（{@code setAllowedOrigins("*")}），实际生产环境建议指定具体域名
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");  // 允许所有来源跨域访问
    }
}