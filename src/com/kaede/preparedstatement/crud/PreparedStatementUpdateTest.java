package com.kaede.preparedstatement.crud;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.kaede.util.JDBCUtils;
import org.junit.jupiter.api.Test;

/**
 * 使用PreparedStatement来替换Statement，实现对数据表的增删改查操作
 * 增删改；查
 * 
 * 好处：
 * 1、PreparedStatement替换Statement解决了拼串导致的sql注入问题
 * 2、PreparedStatement可以操作Blob数据，而Statement做不到
 * 3、PreparedStatement可以实现更高效的批量操作
 */

public class PreparedStatementUpdateTest {
    public static void main(String[] args){
        PreparedStatementUpdateTest.update("delete from customers where id = ?", 3);
        // mysql关键字要用着重号，否则会报错
        // String sql = "update order set order_name = ? where order_id = ?";
        String sql = "update `order` set order_name = ? where order_id = ?";
        PreparedStatementUpdateTest.update(sql, "DD", 2);
    }

    //向customer表中添加一条记录
    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        InputStream is = null;
        //这里不用try with-resources是因为Connection和PreparedStatement是接口，只有实现了AutoCloseable接口
        //的类（实现了close()方法）才可以使用try with- resources
        try {
            //1、读取配置文件中的四个基本信息   
            is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
            
            Properties pros = new Properties();
            pros.load(is);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            //2、加载驱动
            Class.forName(driverClass);

            //3、获取连接
            conn = DriverManager.getConnection(url, user, password);
            
            //4、预编译sql语句，返回PreparedStatement实例
            String sql = "insert into customers(name,email,birth) values(?,?,?)";
            ps = conn.prepareStatement(sql);

            //5、填充占位符
            ps.setString(1,"琴里");
            ps.setString(2, "kotory@gmail.com");
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //java.util.Date date = sdf.parse("2001-08-03");
            //ps.setDate(3, new java.sql.Date(date.getTime()));
            //ps.setDate(3, new java.sql.Date(date.getTime()));
            LocalDate localDate = LocalDate.of(2001, 8, 3);
            long milli = localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
            ps.setDate(3, new java.sql.Date(milli));
            //6、执行操作
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //7、资源的关闭
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
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
    }

    //修改customer表中的一条记录
    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1、获取数据库的连接
            conn = JDBCUtils.getConnection();

            //2、预编译sql语句，返回PreparedStatement的实例
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);

            //3、填充占位符
            ps.setObject(1, "莫扎特");
            ps.setObject(2, 18);

            //4、执行
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5、资源的关闭
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //通用的增删改操作
    public static void update(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1、获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2、预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //3、填充占位符
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //与数据库交互的索引从1开始
            }
            //4、执行
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5、资源的关闭
            JDBCUtils.closeResource(conn, ps);
        }   
    }
}
