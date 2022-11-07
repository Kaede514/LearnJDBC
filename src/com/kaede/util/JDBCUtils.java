package com.kaede.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的工具类
 */

public class JDBCUtils {
    //获取数据库的连接
    public static Connection getConnection() throws Exception {
        //1、读取配置文件中的四个基本信息   
        InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2、加载驱动
        Class.forName(driverClass);

        //3、获取连接
        Connection conn = DriverManager.getConnection(url, user, password);

        if(is != null) is.close();
        return conn;
    }

    //关闭数据库的连接
    public static void closeResource(Connection conn, Statement ps) {
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //关闭资源的操作
    public static void closeResource(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if(rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用Druid数据库连接池技术
     */
    private static DataSource dataSource;

    static {
        InputStream is = null;
        try {
            Properties pros = new Properties();
            is = JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties");
            pros.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(pros);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnectionDruid() throws SQLException {
        Connection conn = dataSource.getConnection();
        return conn;
    }

    /**
     * 使用dbutils.jar中提供的DbUtils工具类，实现资源的关闭
     */
    public static void closeResource1(Connection conn, PreparedStatement ps, ResultSet rs) {
        //方式1：
        /* try {
            DbUtils.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            DbUtils.close(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            DbUtils.close(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } */

        //方式2：
        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);
    }
}
