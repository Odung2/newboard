package com.wemade.newboard.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginParam {

    @NotBlank(message = "아이디를 작성해주세요.")
    @Size(min = 1, max=20, message = "아이디 입력은 1자 이상 20자 이하만 가능합니다.")
    @Schema(description = "사용자 아이디")
    private String userId;

    @NotBlank(message = "비밀번호를 작성해주세요.")
    @Size(min = 1, max=20, message = "비밀번호 입력은 1자 이상 20자 이하만 가능합니다.")
    @Schema(description = "사용자 비밀번호")
    private String password;
}
