package com.cookie.human_flavor_cookie.member.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.member.dto.*;
import com.cookie.human_flavor_cookie.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cookie.human_flavor_cookie.member.entity.Member;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입 성공");
        return ResponseEntity.ok(response);
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

    @PostMapping("/update-target")
    public ResponseEntity<String> updateDailyTarget(
            @RequestBody UpdateTargetRequestDto requestDto,
            @LoginUser Member member) { // 요청 속성에서 로그인된 사용자 정보 사용
        memberService.updateDailyTarget(member, requestDto.getNewTarget());
        return ResponseEntity.ok("Daily target updated successfully.");
    }

    @GetMapping("/main-page")
    public ResponseEntity<?> loginMember(@LoginUser Member member){
        MainPageDto dto = memberService.getMainPage(member);
        return ResponseEntity.ok(dto);
    }
}
