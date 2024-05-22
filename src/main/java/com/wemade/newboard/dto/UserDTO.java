package com.wemade.newboard.dto;

import com.wemade.newboard.param.UpdateUserParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "사용자 정보를 나타내는 DTO")
public class UserDTO extends BaseDTO{

    @Schema(description = "사용자 ID")
    private int userNo;

    @Schema(description = "사용자 아이디")
    private String userId;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "사용자 전화번호")
    private String phone;

    @Schema(description = "사용자 비밀번호")
    private String password;

    public UserDTO(UpdateUserParam updateUserParam, int userNo){
        this.setUserNo(userNo);
        this.setName(updateUserParam.getName());
        this.setEmail(updateUserParam.getEmail());
        this.setPhone(updateUserParam.getPhone());
        this.setPassword(updateUserParam.getPassword());
        this.setUpdatedBy(userNo);
    }

}
