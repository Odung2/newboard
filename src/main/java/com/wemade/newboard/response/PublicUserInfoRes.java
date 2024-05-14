package com.wemade.newboard.response;

import com.wemade.newboard.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PublicUserInfoRes {
    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "만든 일시")
    private LocalDateTime createdAt;

    public PublicUserInfoRes(UserDTO user) {
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
    }
}
