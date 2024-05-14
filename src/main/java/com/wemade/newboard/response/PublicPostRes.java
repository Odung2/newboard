package com.wemade.newboard.response;

import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@Data
public class PublicPostRes {
    @Schema(description = "포스트 아이디")
    private int postNo;
    @Schema(description = "포스트 제목")
    private String title;
    @Schema(description = "사용자 이름")
    private String name;
    @Schema(description = "조회수")
    private int views;
    @Schema(description = "만든 일시")
    private LocalDateTime createdAt;

//    public PublicPostRes(PostDTO post, String nickname){
//        this.postId = post.getPostId();
//        this.title = post.getTitle();
//        this.nickname = nickname;
//        this.views = post.getViews();
//        this.createdAt = post.getCreatedAt();
//    }
}
