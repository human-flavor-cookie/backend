package com.cookie.human_flavor_cookie.member.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.member.dto.*;
import com.cookie.human_flavor_cookie.member.service.MemberService;
import com.cookie.human_flavor_cookie.running.service.RunningService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cookie.human_flavor_cookie.member.entity.Member;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final HttpSession httpSession;
    private final RunningService runningService;

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
    public ResponseEntity<?> updateDailyTarget(
            @RequestBody UpdateTargetRequestDto requestDto,
            @LoginUser Member member) { // 요청 속성에서 로그인된 사용자 정보 사용
        memberService.updateDailyTarget(member, requestDto.getNewTarget());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Daily target updated successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/main-page")
    public ResponseEntity<?> loginMember(@LoginUser Member member) {
        MainPageDto dto = memberService.getMainPage(member);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean isAvailable = memberService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@LoginUser Member member) {
        UserProfileResponseDto userProfile = memberService.getUserProfile(member);
        return ResponseEntity.ok(userProfile);
    }
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(@LoginUser Member member) {
        List<RankingResponseDto> ranking = memberService.getRanking(member);

        // 상위 3명 및 내 랭킹 정보
        Map<String, Object> response = new HashMap<>();
        response.put("top3", ranking.stream().limit(3).toList()); // 상위 3명
        response.put("userRank", ranking.stream()
                .filter(r -> r.getUserName().equals(member.getName()))
                .findFirst()
                .orElse(null)); // 현재 유저의 랭킹 정보
        response.put("allRanks", ranking); // 전체 랭킹

        return ResponseEntity.ok(response);
    }
    @GetMapping("/dailyranking")
    public ResponseEntity<Map<String, Object>> getDailyRanking(@LoginUser Member member) {
        List<DailyRankingResponseDto> dailyRanking = memberService.getDailyRanking(member);

        // 상위 3명 및 내 랭킹 정보
        Map<String, Object> response = new HashMap<>();
        response.put("top3", dailyRanking.stream().limit(3).toList()); // 상위 3명
        response.put("userRank", dailyRanking.stream()
                .filter(r -> r.getUserName().equals(member.getName()))
                .findFirst()
                .orElse(null)); // 현재 유저의 랭킹 정보
        response.put("allRanks", dailyRanking); // 전체 랭킹

        return ResponseEntity.ok(response);
    }
    @GetMapping("/targetranking")
    public ResponseEntity<Map<String, Object>> getTargetRanking(@LoginUser Member member) {
        List<TargetRankingResponseDto> targetRanking = memberService.getTargetRanking(member);

        // 상위 3명 및 내 랭킹 정보
        Map<String, Object> response = new HashMap<>();
        response.put("top3", targetRanking.stream().limit(3).toList()); // 상위 3명
        response.put("userRank", targetRanking.stream()
                .filter(r -> r.getUserName().equals(member.getName()))
                .findFirst()
                .orElse(null)); // 현재 유저의 랭킹 정보
        response.put("allRanks", targetRanking); // 전체 랭킹

        return ResponseEntity.ok(response);
    }
}
