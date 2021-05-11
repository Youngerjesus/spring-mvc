package com.example.demo.basic.config;

import com.example.demo.sample.domain.Member;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Iterator;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType() == Member.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Member member = new Member();
        Iterator<String> headerNames = nativeWebRequest.getHeaderNames();

        while (headerNames.hasNext()){
            String headerKey = headerNames.next();

            if(headerKey == "user-id"){
                member.setId(Long.valueOf(nativeWebRequest.getHeader(headerKey)));
            }
            if(headerKey == "user-nickname"){
                member.setNickname(nativeWebRequest.getHeader(headerKey));
            }
            if(headerKey == "user-role"){
                member.setRole(nativeWebRequest.getHeader(headerKey));
            }
        }

        return member;
    }
}
