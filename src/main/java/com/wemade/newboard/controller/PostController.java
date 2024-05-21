package com.wemade.newboard.controller;

import com.wemade.newboard.dto.PostViewBO;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.param.BasePagingParam;
import com.wemade.newboard.param.UpdatePostParam;
import com.wemade.newboard.response.PublicPostRes;
import com.wemade.newboard.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

//@RestController
@Controller
@RequestMapping("/newboard")
@RequiredArgsConstructor
public class PostController extends BaseController{

    private final PostService postService;

    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @GetMapping("/public/posts/list")
    public String showPostListPage() {
        return "postList";
    }

    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @GetMapping("/public/posts/list/{keyword}")
    public String showSearchPostListPage(
            @PathVariable String keyword,
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return "postList";
    }
    @Operation(summary = "새로운 게시물을 추가합니다.")
    @GetMapping("/public/posts")
    public String showNewPostForm() {
        return "newPostForm";
    }

    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @PostMapping("/posts/list")
    public ResponseEntity<ApiResponse<List<PublicPostRes>>> getPostAllByOffset(
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return ok(postService.getPublicPostIntroAllByOffset(basePagingParam));
    }

    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @PostMapping("/posts/list/{keyword}")
    public ResponseEntity<ApiResponse<List<PublicPostRes>>> searchPosts(
            @PathVariable String keyword,
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return ok(postService.searchPosts(keyword, basePagingParam));
    }

    @Operation(summary = "새로운 게시물을 추가합니다.")
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<String>> insertPost(
            @RequestAttribute("reqId") int userNo,
            MultipartHttpServletRequest request) throws IOException {
        return ok(postService.insertPost(request, userNo));
    }

    @Operation(summary = "특정 게시물을 ID로 조회합니다.")
    @GetMapping("/posts/{postNo}")
    public ResponseEntity<ApiResponse<PostViewBO>> getPostById(
            @PathVariable int postNo) {
        return ok(postService.getPostViewById(postNo));
    }

    @Operation(summary = "유저가 작성한 본인의 게시물을 업데이트(수정)합니다.")
    @PutMapping("/posts/{postNo}")
    public ResponseEntity<ApiResponse<String>> updatePost(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int postNo,
            @RequestBody @Valid UpdatePostParam updatePostParam) throws UnauthorizedAccessException {
        return ok(postService.updatePost(updatePostParam, postNo, userNo));
    }

    @Operation(summary = "유저가 작성한 본인의 게시물을 삭제합니다.")
    @DeleteMapping("/{postNo}")
    public ResponseEntity<ApiResponse<Integer>> deletePost(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int postNo) throws BadRequestException, UnauthorizedAccessException {
        return ok(postService.deletePost(postNo, userNo));
    }

}
