package com.wemade.newboard.response;

import com.wemade.newboard.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PublicUserInfoRes {
    @Schema(description = "사용자 닉네임")
    private String nickname;

    @Schema(description = "만든 일시")
    private LocalDateTime createdAt;

    public PublicUserInfoRes(UserDTO user) {
        this.nickname = user.getName();
        this.createdAt = user.getCreatedAt();
    }
}
