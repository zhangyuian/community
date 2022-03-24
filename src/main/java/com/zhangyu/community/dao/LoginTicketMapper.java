package com.zhangyu.community.dao;

import com.zhangyu.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert({"insert into loginticket (user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLogin(LoginTicket loginTicket);

    @Select({"select * from loginticket where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update loginticket set status = #{status} where ticket = #{ticket}"})
    int updateStatus(String ticket, int status);

}
