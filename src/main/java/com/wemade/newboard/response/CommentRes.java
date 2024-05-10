package com.wemade.newboard.response;

import com.wemade.newboard.dto.CommentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentRes {
    @Schema(description = "댓글 ID")
    public int commentId;
    @Schema(description = "게시물 ID")
    public int postId;
    @Schema(description = "사용자 닉네임")
    public String nickname;
    @Schema(description = "댓글 내용")
    public String commentText;
    @Schema(description = "만든 일시")
    public LocalDateTime createdAt;
    @Schema(description = "최종 변경 일시")
    public LocalDateTime updatedAt;
//
//    public CommentRes(CommentDTO comment, String nickname) {
//        this.nickname = nickname;
//
//        this.postId = comment.getPostId();
//        this.commentText = comment.getCommentText();
//        this.createdAt = comment.getCreatedAt();
//        this.updatedAt = comment.getUpdatedAt();
//    }
//    public CommentRes(CommentDTO comment, String nickname) {
//        this.nickname = nickname;
//
//        this.postId = comment.getPostId();
//        this.commentText = comment.getCommentText();
//        this.createdAt = comment.getCreatedAt();
//        this.updatedAt = comment.getUpdatedAt();
//    }
}
