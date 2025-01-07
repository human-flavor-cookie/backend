package com.cookie.human_flavor_cookie.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingRequestDto {
    private Long friendRequestId;
    private Long requesterId;
}
