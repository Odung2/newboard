package com.wemade.newboard.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindPasswordParam {

    @NotBlank(message = "가입 시 아이디를 작성해주세요.")
    @Schema(description = "사용자 아이디")
    private String userId;

    @NotBlank(message = "새 비밀번호를 발급받기 위해 가입 시 이메일을 작성해주세요.")
    @Schema(description = "사용자 이메일")
    private String email;
}
