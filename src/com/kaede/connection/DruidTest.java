package com.kaede.connection;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DruidTest {

    @Test
    public void getConnction1() throws SQLException {
        DruidDataSource source = new DruidDataSource();
        source.setUsername("root");
        source.setPassword("123456");
        source.setUrl("jdbc:mysql://localhost:3306/test");
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setInitialSize(10);
        source.setMaxActive(20);
        Connection conn = source.getConnection();
        System.out.println(conn);
    }

    @Test
    public void getConnction2() throws Exception {
        Properties pros = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        pros.load(is);
        DataSource source = DruidDataSourceFactory.createDataSource(pros);
        Connection conn = source.getConnection();
        System.out.println(conn);
        if(is != null) {
            is.close();
        }
    }

}
