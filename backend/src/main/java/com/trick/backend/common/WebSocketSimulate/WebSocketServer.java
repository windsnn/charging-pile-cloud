package com.trick.backend.common.WebSocketSimulate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/wx/charging/{orderNo}")
public class WebSocketServer {

    // Key: 订单ID (orderNo) Value: WebSocket的Session对象
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 向指定订单ID的客户端发送消息
     *
     * @param orderNo 订单ID
     * @param message 要发送的消息对象，会自动转为JSON
     */
    public void sendTo(String orderNo, Object message) {
        Session session = sessionMap.get(orderNo);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                sendMessage(session, json);
            } catch (JsonProcessingException e) {
                log.error("【WebSocket】消息序列化失败", e);
            }
        } else {
            log.warn("【WebSocket】推送失败，连接不存在或已关闭, orderNo: {}", orderNo);
        }
    }

    private void sendMessage(Session session, String messageText) {
        try {
            session.getBasicRemote().sendText(messageText);
        } catch (IOException e) {
            log.error("【WebSocket】发送消息异常", e);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("orderNo") String orderNo) {
        sessionMap.put(orderNo, session);
        log.info("【WebSocket】有新的连接，订单ID: {}, 当前连接数: {}", orderNo, sessionMap.size());

        sendMessage(session, "连接成功！");
    }

    @OnClose
    public void onClose(@PathParam("orderNo") String orderNo) {
        sessionMap.remove(orderNo);
        log.info("【WebSocket】连接断开，订单ID: {}, 当前连接数: {}", orderNo, sessionMap.size());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("orderNo") String orderNo) {
        log.info("【WebSocket】收到客户端[{}]的消息: {}", orderNo, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【WebSocket】发生错误", error);
    }
}
