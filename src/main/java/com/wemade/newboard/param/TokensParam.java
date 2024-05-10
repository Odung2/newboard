package com.wemade.newboard.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokensParam {

    @NotBlank(message = "액세스 토큰을 넣어주세요.")
    @Size(min = 1, max=300, message = "토큰은 1자 이상 300자 이하만 가능합니다.")
    @Schema(description = "Access token")
    private String accessToken;

    @NotBlank(message = "리프레시 토큰을 넣어주세요.")
    @Size(min = 1, max=300, message = "토큰은 1자 이상 300자 이하만 가능합니다.")
    @Schema(description = "refresh token")
    private String refreshToken;
}
