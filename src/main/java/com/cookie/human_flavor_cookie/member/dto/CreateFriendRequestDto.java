package com.cookie.human_flavor_cookie.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFriendRequestDto {
    @Email
    @NotBlank
    private String receiverEmail;
}
