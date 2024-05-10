package com.wemade.newboard.controller;

import com.wemade.newboard.param.InsertCommentParam;
import com.wemade.newboard.param.UpdateCommentParam;
import com.wemade.newboard.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("weboard/comments")
@RequiredArgsConstructor
public class CommentController extends BaseController{

    private final CommentService commentService;

    /**
     * 특정 게시물에 새 댓글을 추가합니다.
     * @param postId             댓글이 추가될 게시물의 ID
     * @param insertCommentParam 댓글의 상세 정보를 담고 있는 CommentDTO 객체
     * @return 추가된 CommentDTO와 상태 메시지를 포함하는 ResponseEntity를 반환합니다.
     */
    @Operation(summary = "특정 게시물에 새 댓글을 추가합니다.")
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> insertComment(
            @RequestAttribute("reqId") int id,
            @PathVariable int postId,
            @RequestBody @Valid InsertCommentParam insertCommentParam){
        return ok(commentService.insertComment(insertCommentParam, postId, id));
    }

    /**
     * 기존 댓글을 업데이트합니다.
     * @param commentId          업데이트될 댓글의 ID
     * @param updateCommentParam 업데이트할 내용을 담고 있는 CommentDTO 객체
     * @return 업데이트된 CommentDTO와 상태 메시지를 포함하는 ResponseEntity를 반환합니다.
     */
    @Operation(summary = "기존 댓글을 업데이트합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> updateComment(
            @RequestAttribute("reqId") int id,
            @PathVariable int commentId,
            @RequestBody @Valid UpdateCommentParam updateCommentParam) throws Exception{
        return ok(commentService.updateComment(updateCommentParam, commentId, id));
    }

    /**
     * 기존 댓글을 삭제합니다.
     ** @param commentId 삭제될 댓글의 ID
     * @return 삭제된 댓글의 ID와 상태 메시지를 포함하는 ResponseEntity를 반환합니다.
     */
    @Operation(summary = "기존 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Integer>> deleteComment(
            @RequestAttribute("reqId") int id,
            @PathVariable int commentId) throws Exception {
        return ok(commentService.deleteComment(commentId, id));
    }

}
