package com.wemade.newboard.service;

import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.dto.PostViewBO;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.mapper.PostMapper;
import com.wemade.newboard.param.BasePagingParam;
import com.wemade.newboard.param.InsertPostParam;
import com.wemade.newboard.param.UpdatePostParam;
import com.wemade.newboard.response.DetailPostRes;
import com.wemade.newboard.response.PublicPostRes;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PostService {
    private final PostMapper postMapper;
    private final AuthService authService;
    private final CommentService commentService;
    public List<PostDTO> getPostAll() { return postMapper.getPostAll(); }

    /**
     * 지정된 오프셋부터 모든 게시물을 조회합니다.
     * @param basePagingParam 시작할 위치의 오프셋
     * @return 오프셋 기준으로 조회된 게시물 목록
     */
//    public List<PostDTO> getPostAllByOffset(BasePagingParam basePagingParam) {
//        return postMapper.getPostAllByOffset((int) basePagingParam.getCurrPage(), (int) basePagingParam.getPageSize());
//    }

    public List<PublicPostRes> getPublicPostIntroAllByOffset(BasePagingParam basePagingParam) {
        return postMapper.getPublicPostIntroAllByOffset((int) basePagingParam.getOffset(), (int) basePagingParam.getPageSize());
    }


    /**
     * 주어진 아이디에 해당하는 게시글의 상세 정보를 조회합니다.
     * 게시글 정보와 해당 게시글의 모든 댓글을 포함한 PostViewBO 객체를 반환합니다.
     * @param postId 조회할 게시글의 ID
     * @return 게시글과 댓글 정보를 포함한 PostViewBO 객체
     */
    public PostViewBO getPostViewById(int postId){
        //FIXME: 파일도 불러와야 함...
        PostViewBO postView = new PostViewBO();
        postView.setPost(postMapper.getDetailPostRes(postId));
        postView.setComment(commentService.getCommentRes(postId));

        return postView;
    }

    public PostDTO getPostById(int postId){
        return postMapper.getPostById(postId);
    }

    public List<DetailPostRes> searchPosts(String param) {
        List<DetailPostRes> searchPosts = postMapper.searchPosts(param);
        if (searchPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return searchPosts;
    }

    public List<DetailPostRes> getUserPosts(int id) {
        List<DetailPostRes> userPosts = postMapper.getUserPosts(id);
        if (userPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userPosts;
    }

    public List<DetailPostRes> getUserTempPosts(int id) {
        List<DetailPostRes> userTempPosts = postMapper.getUserTempPosts(id);
        if (userTempPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userTempPosts;
    }




    /**
     * 새로운 게시물을 추가합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시물의 생성자로 설정한 후 데이터베이스에 삽입합니다.
     *
     * @param insertPostParam 삽입할 게시물 데이터
     * @param id              요청자의 id
     * @return 데이터베이스에 삽입된 게시물 객체
     */
    public String insertPost(InsertPostParam insertPostParam, int id){

        PostDTO post = new PostDTO();
        post.setTitle(insertPostParam.getTitle());
        post.setContents(insertPostParam.getContents());
        post.setFileData(insertPostParam.getFileData());
        post.setCreatedBy(id);

        postMapper.insert(post);
        return post.getTitle();
    }

    /**
     * 주어진 ID의 게시물을 업데이트합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시물의 수정자로 설정한 후 데이터베이스에 업데이트합니다.
     *
     * @param updatePostParam 업데이트할 게시물 데이터
     * @param postId          업데이트할 게시물의 ID
     * @param jwttoken        요청자의 JWT 토큰
     * @return 데이터베이스에 업데이트된 게시물 객체
     */
    public String updatePost(UpdatePostParam updatePostParam, int postId, int id) throws UnauthorizedAccessException {

        if(getPostById(postId).getCreatedBy() != id){
            throw new UnauthorizedAccessException("타인의 게시물을 수정할 수 없습니다.");
        }

        PostDTO post = new PostDTO();
        post.setTitle(updatePostParam.getTitle());
        post.setContents(updatePostParam.getContents());
        post.setFileData(updatePostParam.getFileData());
        post.setPostId(postId);
        //FIXME auditListener로 고쳐야 함
        post.setUpdatedBy(id);

        postMapper.update(post);
        return post.getTitle();
    }

    /**
     * 주어진 ID의 게시물을 삭제합니다.
     * JWT 토큰에서 추출한 사용자 ID가 게시물의 생성자 ID와 일치해야 삭제가 가능합니다.
     * @param postId 삭제할 게시물의 ID
     * @param jwttoken 요청자의 JWT 토큰
     * @return 삭제된 게시물의 수 (성공적으로 삭제되면 1)
     * @throws BadRequestException 게시물의 생성자가 아닌 경우 예외를 던집니다.
     */
    public int deletePost(int postId, int id) throws BadRequestException, UnauthorizedAccessException {
        PostDTO post = getPostById(postId);
        if(post==null){
            throw new NotFoundException("해당 게시글이 존재하지 않습니다.");
        }
        if(post.getCreatedBy() != id){
            throw new UnauthorizedAccessException("타인의 게시물을 수정할 수 없습니다.");
        }

        return postMapper.delete(postId);
    }


}
