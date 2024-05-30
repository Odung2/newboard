package com.wemade.newboard.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class SignupParam {

    @Pattern(regexp = "^[a-z0-9]{3,15}$", message = "아이디는 영어, 숫자 조합 3~15자만 입력 가능합니다.")
    @Schema(description = "사용자 아이디")
    private String userId;

    @Pattern(regexp = "^[가-힣]{2,20}$", message = "한글, 2~20자만 입력 가능합니다.")
    @Schema(description = "사용자 이름")
    private String name;

    @Email
    @Size(max=100, message = "이메일은 100자 이하만 가능합니다.")
    @Schema(description = "사용자 이메일")
    private String email;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "숫자만 입력해주세요.")
    @Schema(description = "사용자 전화번호")
    private String phone;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호 규격에 맞지 않습니다.")
    @Schema(description = "사용자 비밀번호")
    private String password;

}
