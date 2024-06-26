package com.wemade.newboard.service;

import com.wemade.newboard.dto.CommentDTO;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.mapper.CommentMapper;
import com.wemade.newboard.mapper.PostMapper;
import com.wemade.newboard.param.InsertCommentParam;
import com.wemade.newboard.param.UpdateCommentParam;
import com.wemade.newboard.response.CommentRes;
import com.wemade.newboard.response.DetailPostRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final AuthService authService;

    public List<CommentDTO> getCommentByPostId(int postId){
        return commentMapper.getCommentByPostId(postId);
    }

    /**
     * 댓글 조회
     * @param commentId
     * @return
     */
    public CommentDTO getCommentByCommentId(int commentId){
        return commentMapper.getCommentByCommentId(commentId);
    }

    /**
     * 댓글 반환
     * @param postId
     * @return
     */
    public List<CommentRes> getCommentRes(int postId) {
        return commentMapper.getComments(postId);
    }

    /**
     * 댓글 작성
     * @param insertCommentParam
     * @param postNo
     * @param userNo
     * @return
     */
    @Transactional
    public String insertComment(InsertCommentParam insertCommentParam, int postNo, int userNo){

        // 존재하는 게시글인지 검사
        if(postMapper.getPostById(postNo)==null){
            throw new NotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        CommentDTO comment = new CommentDTO();
        comment.setContents(insertCommentParam.getContents());
        comment.setPostNo(postNo);
        comment.setUserNo(userNo);

        commentMapper.insert(comment);
        return comment.getContents();
    }

    /**
     * 기존의 댓글을 업데이트합니다.
     * 댓글 ID를 설정하고, 수정된 내용을 데이터베이스에 업데이트합니다.
     *
     * @param updateCommentParam 업데이트할 댓글 데이터
     * @param commentId          업데이트될 댓글의 ID
     * @return 데이터베이스에 업데이트된 댓글 객체
     */
    @Transactional
    public String updateComment(UpdateCommentParam updateCommentParam, int commentId, int id) throws UnauthorizedAccessException {
        // 본인이 쓴 댓글이 아닐 경우
        if(getCommentByCommentId(commentId).getUserNo() != id){
            throw new UnauthorizedAccessException("타인의 댓글을 수정할 수 없습니다.");
        }

        CommentDTO comment = new CommentDTO();
        comment.setContents(updateCommentParam.getContents());
        comment.setUserNo(id);
        comment.setCommentNo(commentId);
        comment.setUpdatedBy(id);

        commentMapper.update(comment);
        return comment.contents;
    }

    /**
     * 특정 댓글을 삭제합니다.
     * 댓글 ID를 사용하여 해당 댓글을 데이터베이스에서 삭제합니다.
     * @param commentId 삭제할 댓글의 ID
     * @return 삭제 결과 (성공 시 1, 실패 시 0)
     */
    @Transactional
    public int deleteComment(int commentId, int id) throws UnauthorizedAccessException {
        CommentDTO comment = getCommentByCommentId(commentId);
        if(comment==null) throw new NotFoundException("해당 댓글이 존재하지 않습니다.");
        if(comment.getUserNo() != id) throw new UnauthorizedAccessException("타인의 댓글을 삭제할 수 없습니다.");

        return commentMapper.delete(commentId);
    }

    /**
     * 댓글 존재 확인
     * @param id
     * @return
     */
    public List<CommentRes> getUserComments(int id) {
        List<CommentRes> userComments= commentMapper.getUserComments(id);
        if (userComments == null) {
            throw new NotFoundException("댓글을 찾을 수 없습니다.");
        }
        return userComments;
    }

}
