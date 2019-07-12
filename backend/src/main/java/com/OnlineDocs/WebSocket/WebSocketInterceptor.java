package com.OnlineDocs.WebSocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


/**
 *@Title WebSocketInterceptor.java
 *@description:  WebSocket 适配器 拦截器
 *@time 创建时间：2018年4月26日 上午9:16:09
 **/
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    private final static Logger log=Logger.getLogger(WebSocketInterceptor.class);

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception exceptions) {
        log.info("=================执行 afterHandshake ：handler: "+handler+"exceptions: "+exceptions);
        super.afterHandshake(request, response, handler, exceptions);
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler,
                                   Map<String, Object> map) throws Exception {
        log.info("==================执行 beforeHandshake ：handler: "+handler+"map: "+map.values());
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String id = servletRequest.getSession().getId();
        System.out.println("beforeHandshake: \n"+id);
        String username = servletRequest.getParameter("username");
        map.put("username",username);
        String docName = servletRequest.getParameter("docName");
        map.put("docName",docName);
        String docOwner = servletRequest.getParameter("docOwner");
        map.put("docOwner",docOwner);

        return super.beforeHandshake(request, response, handler, map);
    }

}