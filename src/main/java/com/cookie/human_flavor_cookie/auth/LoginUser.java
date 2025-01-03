package com.cookie.human_flavor_cookie.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)  //메소드의 파라미터로 선언된 객체에서 사용가능
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {   //LoginMember라는 어노테이션 생성
}
