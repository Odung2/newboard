package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.UserDTO;
import com.wemade.newboard.param.SignupParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper{
    UserDTO getUserByUserId(String emailOrUserId);
    UserDTO getUserByUserNo(int userNo);

    String getEmail(String userId);

    UserDTO getUserByEmail(String email);
    String getPasswordById(int id);
    int insert(SignupParam signupParam);

    int update(UserDTO user);

    int delete(int id);


}
