package com.amoa.server.domain.user.converter;

import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;

public class UserConverter {

    public static NicknameCheckResDTO toNicknameCheckResDTO(String nickname, boolean available) {
        return new NicknameCheckResDTO(nickname, available);
    }
}