package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.response.DetailPostRes;
import com.wemade.newboard.response.PublicPostRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper {

    PostDTO getPostById(int postId);

    List<PostDTO> getPostAll();
    List<PostDTO> getPostAllByOffset(@Param("offset") int currpage, @Param("pagesize") int pagesize);
    List<DetailPostRes> getUserPosts(int userNo);
    List<DetailPostRes> getUserTempPosts(int userNo);
    List<PublicPostRes> searchPosts(@Param("keyword") String keyword, @Param("offset") int currpage, @Param("pagesize") int pagesize );
    int insert(PostDTO post);
    int update(PostDTO post);
    @Override
    int delete(int postId);
    DetailPostRes getDetailPostRes(int postId);
    List<PublicPostRes> getPublicPostIntroAllByOffset(@Param("offset") int currPage, @Param("pagesize") int pageSize);
}
