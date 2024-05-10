package com.wemade.newboard.response;

import com.wemade.newboard.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MyInfoRes {
    @Schema(description = "사용자 아이디")
    private String userId;
    @Schema(description = "사용자 닉네임")
    private String nickname;
    @Schema(description = "만든 일시")
    private LocalDateTime createdAt;
    @Schema(description = "마지막 변경 일시")
    private LocalDateTime updatedAt;

    public MyInfoRes(UserDTO user){
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
