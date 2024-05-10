package com.wemade.newboard.controller;

import com.wemade.newboard.dto.PostViewBO;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.param.BasePagingParam;
import com.wemade.newboard.param.InsertPostParam;
import com.wemade.newboard.param.UpdatePostParam;
import com.wemade.newboard.response.PublicPostRes;
import com.wemade.newboard.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("weboard/posts")
@RequiredArgsConstructor
public class PostController extends BaseController{

    private final PostService postService;

    /**
     * 모든 게시물을 오프셋 기준으로 n개씩 반환합니다.
     *
     * @param basePagingParam 오프셋 값
     * @return 게시물 목록과 상태 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "모든 게시물을 오프셋 기준으로 n개씩 반환합니다.")
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<List<PublicPostRes>>> getPostAllByOffset(
            @RequestBody @Valid BasePagingParam basePagingParam) {
        return ok(postService.getPublicPostIntroAllByOffset(basePagingParam));
    }

    /**
     * 새로운 게시물을 추가합니다.
     *
     * @param insertPostParam 추가할 게시물 데이터
     * @return 추가된 게시물과 상태 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "새로운 게시물을 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> insertPost(
            @RequestAttribute("reqId") int id,
            @RequestBody @Valid InsertPostParam insertPostParam) {
        return ok(postService.insertPost(insertPostParam, id));
    }

    /**
     * 특정 게시물을 ID로 조회합니다.
     *
     * @param postNo 게시물 번호
     * @return 조회된 게시물과 상태 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "특정 게시물을 ID로 조회합니다.")
    @GetMapping("/{postNo}")
    public ResponseEntity<ApiResponse<PostViewBO>> getPostById(
            @PathVariable int postNo) {
        return ok(postService.getPostViewById(postNo));
    }

    /**
     * 유저가 작성한 본인의 게시물을 업데이트(수정)합니다.
     *
     * @param postNo          업데이트할 게시물의 ID
     * @param updatePostParam 업데이트할 게시물 데이터
     * @return 업데이트된 게시물과 상태 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "유저가 작성한 본인의 게시물을 업데이트(수정)합니다.")
    @PutMapping("/{postNo}")
    public ResponseEntity<ApiResponse<String>> updatePost(
            @RequestAttribute("reqId") int id,
            @PathVariable int postNo,
            @RequestBody @Valid UpdatePostParam updatePostParam) throws UnauthorizedAccessException {
        return ok(postService.updatePost(updatePostParam, postNo, id));
    }

    /**
     * 유저가 작성한 본인의 게시물을 삭제합니다.
     *
     * @param postNo 삭제할 게시물의 ID
     * @return 삭제된 게시물 ID와 상태 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "유저가 작성한 본인의 게시물을 삭제합니다.")
    @DeleteMapping("/{postNo}")
    public ResponseEntity<ApiResponse<Integer>> deletePost(
            @RequestAttribute("reqId") int id,
            @PathVariable int postNo) throws BadRequestException, UnauthorizedAccessException {
        return ok(postService.deletePost(postNo, id));
    }

}
