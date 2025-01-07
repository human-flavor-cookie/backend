package com.cookie.human_flavor_cookie.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespondFriendRequestDto {

    @NotNull
    private Long friendRequestId; // 처리할 친구 요청의 ID

    @NotBlank
    private String action; // "ACCEPT" or "REJECT"
}
