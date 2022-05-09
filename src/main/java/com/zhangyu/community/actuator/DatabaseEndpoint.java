package com.zhangyu.community.actuator;

import com.zhangyu.community.utils.CommunityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: zhang
 * @date: 2022/5/7
 * @description:
 */
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation // 表示只能用GET请求来访问
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection()
        ) {
            return CommunityUtils.getJSONString(0, "获取连接成功！");
        } catch (SQLException e) {
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtils.getJSONString(1, "获取连接失败！");
        }
    }
}
