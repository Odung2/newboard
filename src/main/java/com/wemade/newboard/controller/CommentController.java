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
@RequestMapping("/newboard/comments")
@RequiredArgsConstructor
public class CommentController extends BaseController{

    private final CommentService commentService;

    @Operation(summary = "특정 게시물에 새 댓글을 추가합니다.")
    @PostMapping("/{postNo}")
    public ResponseEntity<ApiResponse<String>> insertComment(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int postNo,
            @RequestBody @Valid InsertCommentParam insertCommentParam){
        return ok(commentService.insertComment(insertCommentParam, postNo, userNo));
    }


    @Operation(summary = "기존 댓글을 업데이트합니다.")
    @PutMapping("/{commentNo}")
    public ResponseEntity<ApiResponse<String>> updateComment(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int commentNo,
            @RequestBody @Valid UpdateCommentParam updateCommentParam) throws Exception{
        return ok(commentService.updateComment(updateCommentParam, commentNo, userNo));
    }


    @Operation(summary = "기존 댓글을 삭제합니다.")
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<ApiResponse<Integer>> deleteComment(
            @RequestAttribute("reqId") int userNo,
            @PathVariable int commentNo) throws Exception {
        return ok(commentService.deleteComment(commentNo, userNo));
    }

}
