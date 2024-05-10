package com.wemade.newboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "댓글 정보를 담는 DTO")
public class CommentDTO extends BaseDTO {

    @Schema(description = "댓글 번호")
    public int commentNo;

    @Schema(description = "게시물 번호")
    public int postNo;

    @Schema(description = "사용자 번호")
    public int userNo;

    @Schema(description = "댓글 내용")
    public String contents;

}
