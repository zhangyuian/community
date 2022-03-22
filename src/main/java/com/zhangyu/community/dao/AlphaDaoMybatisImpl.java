package com.zhangyu.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */
@Repository
@Primary
public class AlphaDaoMybatisImpl implements AlphaDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
