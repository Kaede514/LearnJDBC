package com.kaede.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;

/**
 * 操作数据库的工具类
 */

public class JDBCUtils {
    //获取数据库的连接
    public static Connection getConnection() throws Exception {
        //1、读取配置文件中的四个基本信息   
        FileInputStream fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\jdbc.properties");
        Properties pros = new Properties();
        pros.load(fis);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2、加载驱动
        Class.forName(driverClass);

        //3、获取连接
        Connection conn = DriverManager.getConnection(url, user, password);

        if(fis != null) fis.close();
        return conn;
    }

    //关闭数据库的连接
    public static void closeResource(Connection conn, PreparedStatement ps) {
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
    private static DataSource source1;
    static {
        InputStream fis = null;
        try {
            Properties pros = new Properties();
            fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\druid.properties");
            pros.load(fis);
            source1 = DruidDataSourceFactory.createDataSource(pros);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Connection getConnection1() throws SQLException {
        Connection conn = source1.getConnection();
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
