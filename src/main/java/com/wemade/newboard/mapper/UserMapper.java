package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.UserDTO;
import com.wemade.newboard.param.SignupParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper{
    UserDTO getUserByIdOrUserId(int id);
    UserDTO getUserByIdOrUserId(UserDTO user);


    UserDTO getUserByUserId(String emailOrUserId);
    UserDTO getUserByUserNo(int userNo);

    String getEmail(String userId);

    String doesEmailExist(String email);

    UserDTO getUserByEmail(String email);
    int getIdByUserId(String userId);
    String getPasswordById(int id);
    int insert(SignupParam signupParam);

    int update(UserDTO user);

    int delete(int id);


}
