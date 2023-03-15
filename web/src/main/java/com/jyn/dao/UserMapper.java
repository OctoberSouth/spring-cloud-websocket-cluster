package com.jyn.dao;

import com.jyn.entity.User;
import com.jyn.util.MyMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 10263
 */
@Mapper
public interface UserMapper extends MyMapper<User> {

}
