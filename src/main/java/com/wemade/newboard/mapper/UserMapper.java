package com.wemade.newboard.mapper;

import com.wemade.newboard.dto.UserDTO;
import com.wemade.newboard.param.SignupParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper{
    UserDTO getUserByIdOrUserId(int id);
    UserDTO getUserByIdOrUserId(UserDTO user);


    UserDTO getUser(String emailOrUserId, String type);
    UserDTO getUser(int userNo);

    UserDTO getUserByEmail(String email);
    int getIdByUserId(String userId);
    String getPasswordById(int id);
    int addLoginFailCount(int id);
    int resetLoginFailCount(int id);

    int resetLoginLocked(int id);
    int lockUnlockUser(@Param("id") int id, @Param("isLocked") int isLocked);
    int updateLoginLocked(UserDTO user);

    int insert(SignupParam signupParam);

    int update(UserDTO user);

    int delete(int id);


}
