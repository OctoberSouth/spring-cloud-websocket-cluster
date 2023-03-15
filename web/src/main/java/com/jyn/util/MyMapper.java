package com.jyn.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author 10263
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}

