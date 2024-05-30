package com.wemade.newboard.dto;

import com.wemade.newboard.response.PublicPostRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@Schema(description = "검색 게시물 갯수 및 페이징 처리")
public class PostListDTO {
    int totalPages;
    List<PublicPostRes> searchPosts;
}
