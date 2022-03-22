package com.zhangyu.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */

@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
