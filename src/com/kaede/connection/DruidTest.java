package com.kaede.connection;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import org.junit.Test;

public class DruidTest {

    @Test
    public void getConnction() throws Exception {
        Properties pros = new Properties();
        InputStream fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\druid.properties");
        pros.load(fis);

        DataSource source = DruidDataSourceFactory.createDataSource(pros);
        Connection conn = source.getConnection();
        System.out.println(conn);

        if(fis != null)     
            fis.close();
    }

}
