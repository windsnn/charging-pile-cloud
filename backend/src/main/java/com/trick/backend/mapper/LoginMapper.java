package com.trick.backend.mapper;

import com.trick.backend.model.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select("select id,username,password from admin where username = #{username}")
    Admin getAdminByUsername(String username);
}
