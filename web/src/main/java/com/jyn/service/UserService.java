package com.jyn.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jyn.dao.UserMapper;
import com.jyn.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 10263
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 添加
     *
     * @param user
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(User user) {
        this.userMapper.insertUseGeneratedKeys(user);
        System.out.println(user.getId() + "=================");
//        this.userMapper.insert(user);
    }

    /**
     * 分页使用
     *
     * @return
     */
    public PageInfo<User> page(int offset, int limit) {
        PageHelper.offsetPage(offset, limit);
        List<User> list = this.userMapper.selectAll();
        //用PageInfo对结果进行包装
        PageInfo page = new PageInfo(list);
        return page;
    }

}
