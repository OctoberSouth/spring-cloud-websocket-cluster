package com.lp.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author lp
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
