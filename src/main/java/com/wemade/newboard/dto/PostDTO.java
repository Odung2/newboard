package com.wemade.newboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "게시물 정보를 담는 DTO")
public class PostDTO extends BaseDTO{

    @Schema(description = "게시물 번호")
    private int postNo;

    @Schema(description = "게시물 작성한 유저 번호")
    private int userNo;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String contents;

    @Schema(description = "임시 저장 여부")
    private Boolean isTemp;

    public void setTemp(String param) {
        this.setIsTemp(false);
        if (param != null) if (param.equals("1")) this.setIsTemp(true);
    }

    public void setTemp(boolean param){
        this.setIsTemp(param);
    }
}
