package com.OnlineDocs.WebSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


/**
 *@Title WebSocketConfig.java
 *@description:  websocket配置
 *@time 创建时间：2018年4月26日 上午9:12:44
 **/
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/socketServer").addInterceptors(new WebSocketInterceptor()).setAllowedOrigins("*");
        registry.addHandler(myHandler(), "/socketServer/sockjs").addInterceptors(new WebSocketInterceptor()).setAllowedOrigins("*");
    }


    @Bean
    public WebSocketHandler myHandler(){
        return new SocketHandler();
    }


    /**
     * @Title: createWebSocketContainer
     * @Description: 配置webSocket引擎    用于tomcat 可以不配置
     * @param @return
     * @return ServletServerContainerFactoryBean
     * @date createTime：2018年4月26日上午11:18:28
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

}
