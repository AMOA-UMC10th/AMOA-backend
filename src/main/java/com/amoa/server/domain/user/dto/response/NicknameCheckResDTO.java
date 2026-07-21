package com.amoa.server.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckResDTO {
    private String nickname;
    private boolean available;
}