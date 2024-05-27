package com.wemade.newboard.controller;

import com.wemade.newboard.param.InsertCommentParam;
import com.wemade.newboard.param.UpdateCommentParam;
import com.wemade.newboard.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newboard/comments")
@RequiredArgsConstructor
public class CommentController extends BaseController{

    private final CommentService commentService;

    /**
     * 댓글 작성
     * @param userNo
     * @param postNo
     * @param insertCommentParam
     * @return
     */
    @Operation(summary = "특정 게시물에 새 댓글을 추가합니다.")
    @PostMapping("/{postNo}")
    public ResponseEntity<ApiResponse<String>> insertComment(
            @RequestAttribute("reqId") int userNo,
            @Valid @Min(value=1, message = "1 이상 입력만 가능합니다.") @PathVariable int postNo,
            @RequestBody @Valid InsertCommentParam insertCommentParam){
        return ok(commentService.insertComment(insertCommentParam, postNo, userNo));
    }

    /**
     * 댓글 수정
     * @param userNo 유저번호
     * @param commentNo 댓글번호
     * @param updateCommentParam 수정 댓글 내용
     * @return
     */
    @Operation(summary = "기존 댓글을 업데이트합니다.")
    @PutMapping("/{commentNo}")
    public ResponseEntity<ApiResponse<String>> updateComment(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int commentNo,
            @RequestBody @Valid UpdateCommentParam updateCommentParam){
        return ok(commentService.updateComment(updateCommentParam, commentNo, userNo));
    }

    /**
     * 댓글 삭제
     * @param userNo
     * @param commentNo
     * @return
     */
    @Operation(summary = "기존 댓글을 삭제합니다.")
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<ApiResponse<Integer>> deleteComment(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int commentNo) {
        return ok(commentService.deleteComment(commentNo, userNo));
    }

}
