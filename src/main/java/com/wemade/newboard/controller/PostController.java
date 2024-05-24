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
// File io
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.wemade.newboard.dto.FrkConstants.uploadPath;

//@RestController
@Controller
@RequestMapping("/newboard")
@RequiredArgsConstructor
public class PostController extends BaseController{

    private final PostService postService;

    /**
     * html 게시물 리스트
     * @return
     */
    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @GetMapping("/public/posts/list")
    public String showPostListPage() {
        return "postList";
    }

    /**
     * html 게시물 검색
     * @param keyword
     * @param basePagingParam
     * @return
     */
    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @GetMapping("/public/posts/list/{keyword}")
    public String showSearchPostListPage(
            @PathVariable String keyword,
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return "postList";
    }

    /**
     * html 게시물 작성
     * @return
     */
    @Operation(summary = "새로운 게시물을 추가합니다.")
    @GetMapping("/public/posts")
    public String showNewPostForm() {
        return "newPostForm";
    }

    /**
     * 게시물 상세 보기
     * @param postNo
     * @return
     */
    @Operation(summary = "특정 게시물을 ID로 조회합니다.")
    @GetMapping("public/posts/{postNo}")
    public String showPostDetail(@PathVariable int postNo) {
        return "postDetail";
    }


    /**
     * 게시물 리스트
     * @param basePagingParam
     * @return
     */
    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @PostMapping("/posts/list")
    public ResponseEntity<ApiResponse<List<PublicPostRes>>> getPostAllByOffset(
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return ok(postService.getPublicPostIntroAllByOffset(basePagingParam));
    }

    /**
     * 게시물 검색
     * @param keyword
     * @param basePagingParam
     * @return
     */
    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @PostMapping("/posts/list/{keyword}")
    public ResponseEntity<ApiResponse<List<PublicPostRes>>> searchPosts(
            @PathVariable String keyword,
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return ok(postService.searchPosts(keyword, basePagingParam));
    }

    /**
     * 게시물 작성
     * @param userNo
     * @param request
     * @return
     * @throws IOException
     */
    @Operation(summary = "새로운 게시물을 추가합니다.")
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<String>> insertPost(
            @RequestAttribute("reqId") int userNo,
            MultipartHttpServletRequest request) throws IOException {
        return ok(postService.insertPost(request, userNo));
    }

    /**
     * 게시물 상세 보기
     * @param postNo
     * @return
     */
    @Operation(summary = "특정 게시물을 ID로 조회합니다.")
    @GetMapping("/posts/{postNo}")
    public ResponseEntity<ApiResponse<PostViewBO>> getPostById(
            @PathVariable int postNo) {
        return ok(postService.getPostViewById(postNo));
    }

    /**
     * 게시물 수정
     * @param userNo
     * @param postNo
     * @param updatePostParam
     * @return
     * @throws UnauthorizedAccessException
     */
    @Operation(summary = "유저가 작성한 본인의 게시물을 업데이트(수정)합니다.")
    @PutMapping("/posts/{postNo}")
    public ResponseEntity<ApiResponse<String>> updatePost(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int postNo,
            @RequestBody @Valid UpdatePostParam updatePostParam) throws UnauthorizedAccessException {
        return ok(postService.updatePost(updatePostParam, postNo, userNo));
    }

    /**
     * 게시물 삭제
     * @param userNo
     * @param postNo
     * @return
     * @throws BadRequestException
     * @throws UnauthorizedAccessException
     */
    @Operation(summary = "유저가 작성한 본인의 게시물을 삭제합니다.")
    @DeleteMapping("/{postNo}")
    public ResponseEntity<ApiResponse<Integer>> deletePost(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int postNo) throws BadRequestException, UnauthorizedAccessException {
        return ok(postService.deletePost(postNo, userNo));
    }

    public ResponseEntity<Resource> showImage(@RequestParam String imageName) throws UnsupportedEncodingException {

        String decodedImageName = URLDecoder.decode(imageName, StandardCharsets.UTF_8.name());

        // 이미지 파일을 Resource 객체로 로드
        Resource resource = new FileSystemResource(decodedImageName);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        // 이미지 파일을 응답으로 반환
        return ResponseEntity.ok()
             .contentType(MediaType.IMAGE_PNG) // 이미지 타입에 따라 변경
             .body(resource);
    }

}
