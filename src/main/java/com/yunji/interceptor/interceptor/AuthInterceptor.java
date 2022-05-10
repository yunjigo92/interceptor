package com.yunji.interceptor.interceptor;

import com.yunji.interceptor.annotation.Auth;
import com.yunji.interceptor.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        URI uri = UriComponentsBuilder.fromUriString(request.getRequestURI()).query(request.getQueryString()).build().toUri();
        log.info("request url : {}", uri);

        boolean hasAnnotaion = checkAnnotation(handler, Auth.class);
        log.info("has annotation : {}", hasAnnotaion);

        // 나의 서버는 모두 public으로 동작하는데,
        // 단 Auth 권한을 요청에 대해서는 세션 쿠키 관리
        if(hasAnnotaion){
            String query = uri.getQuery();
            if(query.equals("name=yunji")){
                return true;
            }
            throw new AuthException();
        }

        return false;
    }

    private boolean checkAnnotation(Object handler, Class clazz){
        //resource
        if( handler instanceof ResourceHttpRequestHandler){
            return true;
        }

        // annotaion check
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if(null != handlerMethod.getMethodAnnotation(clazz) || null != handlerMethod.getBeanType().getAnnotation(clazz)){

            //annotation이 있을 때는 true 리턴
            return true;
        }
        return false;
    }
}
