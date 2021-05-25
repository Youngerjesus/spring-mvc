package com.example.demo.basic.config;

import com.example.demo.sample.domain.Member;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType() == Member.class;
    }

    @Override
    public Member resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Member member = new Member();
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

        String memberNickName = request.getHeader("user-nickname");
        String memberId = request.getHeader("user-id");
        String memberRole = request.getHeader("user-role");

        member.setRole(memberRole);
        member.setNickname(memberNickName);
        member.setId(Long.valueOf(memberId));
        return member;
    }
}
