package com.wemade.newboard.response;

import com.wemade.newboard.dto.PostDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class DetailPostRes {

    @Schema(description = "게시물 ID")
    private int postNo;
    @Schema(description = "제목")
    private String title;
    @Schema(description = "사용자 이름")
    private String name;
    @Schema(description = "내용")
    private String contents;
    @Schema(description = "작성일")
    private LocalDateTime createdAt;
}
