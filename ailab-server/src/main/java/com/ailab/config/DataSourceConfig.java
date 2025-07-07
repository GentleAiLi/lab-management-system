package com.ailab.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 数据源配置类
 * 实现ApplicationRunner接口，在应用启动时预热连接池
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig implements ApplicationRunner, Ordered {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始预热数据库连接池...");
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        int minIdle = hikariDataSource.getMinimumIdle();
        
        try {
            // 预热连接池，创建最小空闲连接数量的连接
            Connection[] connections = new Connection[minIdle];
            for (int i = 0; i < minIdle; i++) {
                connections[i] = dataSource.getConnection();
                // 执行一个简单的查询来确保连接是有效的
                connections[i].createStatement().execute("SELECT 1");
            }

            // 关闭连接，归还到连接池
            for (int i = 0; i < minIdle; i++) {
                if (connections[i] != null && !connections[i].isClosed()) {
                    connections[i].close();
                }
            }

        } catch (SQLException e) {
            log.error("数据库连接池预热失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public int getOrder() {
        // 设置较高优先级，确保在应用启动早期执行
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}