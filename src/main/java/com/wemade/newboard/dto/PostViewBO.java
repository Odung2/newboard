package com.wemade.newboard.dto;

import com.wemade.newboard.response.CommentRes;
import com.wemade.newboard.response.DetailPostRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "게시물 및 해당 게시글의 댓글 정보를 담는 비즈니스 객체")
public class PostViewBO {

    @Schema(description = "게시물 정보")
    private DetailPostRes post;

    @Schema(description = "댓글 목록")
    private List<CommentRes> comment;

    @Schema(description = "파일 정보")
    private List<FileDTO> files;

}
