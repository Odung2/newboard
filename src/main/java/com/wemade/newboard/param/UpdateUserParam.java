package com.wemade.newboard.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserParam {

    @Nullable
    @Size(min = 2, max=20, message = "아이디는 2자 이상 20자 이하만 가능합니다.")
    @Schema(description = "사용자 아이디")
    private String userId;

    @Nullable
    @Size(min = 1, max=20, message = "이름은 1자 이상 20자 이하만 가능합니다.")
    @Pattern(regexp = "^[가-힣]{1,20}$", message = "한글만 입력해주세요.")
    @Schema(description = "사용자 이름")
    private String name;

    @Nullable
    @Size(min = 1, max=20, message = "이메일은 1자 이상 20자 이하만 가능합니다.")
    @Schema(description = "사용자 이메일")
    private String email;

    @Nullable
    @Size(min = 5, max=20, message = "전화번호는 5자 이상 20자 이하만 가능합니다.")
    @Pattern(regexp = "^[0-9]{5,20}$", message = "숫자만 입력해주세요.")
    @Schema(description = "사용자 전화번호")
    private String phone;

    @Nullable
    @Size(min = 8, max=16, message = "8자 이상 16자 이하의 비밀번호만 가능합니다.")
    @Schema(description = "사용자 비밀번호")
    private String password;
}
