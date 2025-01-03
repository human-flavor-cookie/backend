package com.cookie.human_flavor_cookie.auth;

import com.cookie.human_flavor_cookie.auth.jwt.JwtTokenProvider;
import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Member;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // LoginUser 어노테이션이 붙어 있고, 타입이 Member인 경우 지원
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = Member.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        // 요청 헤더에서 토큰 추출
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = resolveToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 유효하지 않은 경우 예외 처리
        }

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmail(token);

        // 이메일로 사용자 조회
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_MEMBER)); // 사용자 없을 시 예외 처리
    }

    // Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
        }
        return null;
    }
}
