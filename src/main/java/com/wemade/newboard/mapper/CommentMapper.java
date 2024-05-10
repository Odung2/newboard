package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.CommentDTO;
import com.wemade.newboard.response.CommentRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper{
    List<CommentDTO> getCommentByPostId(int postId);
    CommentDTO getCommentByCommentId(int commentId);

    int insert(CommentDTO comment);

    int update(CommentDTO comment);
    @Override
    int delete(int commentId);

    List<CommentRes> getComments(int postId);
}
