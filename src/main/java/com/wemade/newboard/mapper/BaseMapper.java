package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.CommentDTO;
import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.dto.UserDTO;

public interface BaseMapper<T> {
    //FIXME <T> 라는 지네릭스를 써보자!
    int insert(T t);
    int update(T t);
    int delete(int no);

}
