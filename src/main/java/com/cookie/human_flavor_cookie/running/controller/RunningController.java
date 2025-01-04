package com.cookie.human_flavor_cookie.running.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.running.dto.EndRunRequestDto;
import com.cookie.human_flavor_cookie.running.dto.EndRunResponseDto;
import com.cookie.human_flavor_cookie.running.service.RunningService;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/running")
public class RunningController {
    private final RunningService runningService;

    @PostMapping("/end")
    public ResponseEntity<EndRunResponseDto> endRun(
            @RequestBody EndRunRequestDto requestDto,
            @LoginUser Member member) {
        EndRunResponseDto responseDto = runningService.endRun(
                member,
                requestDto.getDistance(),
                requestDto.getDuration()
        );
        return ResponseEntity.ok(responseDto);
    }
}
