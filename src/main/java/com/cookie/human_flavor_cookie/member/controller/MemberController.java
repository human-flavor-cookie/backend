package com.cookie.human_flavor_cookie.member.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.member.dto.ReturnLoginDto;
import com.cookie.human_flavor_cookie.member.service.MemberService;
import com.cookie.human_flavor_cookie.member.dto.SignupDto
import com.cookie.human_flavor_cookie.member.dto.LoginDto
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final HttpSession httpSession;

    //자체 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) throws Exception {
        memberService.signup(signupDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    //자체 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) throws Exception {
        ReturnLoginDto returnDto = memberService.login(loginDto);
        return ResponseEntity.ok(returnDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@LoginUser Member member){
        memberService.logout(member);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/isLogin")
    public ResponseEntity<?> isLogin(@LoginUser Member member){
        return ResponseEntity.ok(member);
    }
}
