package com.kaede.connection;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {

    @Test
    public void testConnection1() {
        try {
            //1.提供java.sql.Driver接口实现类的对象
            Driver driver = new com.mysql.jdbc.Driver();
            //2.提供url，指明具体操作的数据
            String url = "jdbc:mysql://localhost:3306/test";
            //3.提供Properties的对象，指明用户名和密码
            Properties info = new Properties();
            info.setProperty("user", "root");
            info.setProperty("password", "123456");
            //4.调用driver的connect()，获取连接
            Connection conn = driver.connect(url, info);
            System.out.println(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection2() {
        try {
            //1.实例化Driver
            String className = "com.mysql.jdbc.Driver";
            Class clazz = Class.forName(className);
            Driver driver = (Driver) clazz.newInstance();
            //2.提供url，指明具体操作的数据
            String url = "jdbc:mysql://localhost:3306/test";
            //3.提供Properties的对象，指明用户名和密码
            Properties info = new Properties();
            info.setProperty("user", "root");
            info.setProperty("password", "123456");
            //4.调用driver的connect()，获取连接
            Connection conn = driver.connect(url, info);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection3() {
        try {
            //1.数据库连接的4个基本要素
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "123456";
            String driverName = "com.mysql.jdbc.Driver";
            //2.实例化Driver
            Class clazz = Class.forName(driverName);
            Driver driver = (Driver) clazz.newInstance();
            //3.注册驱动
            DriverManager.registerDriver(driver);
            //4.获取连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection4() {
        try {
            //1、提供另外三个基本连接的信息
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "123456";
            //2、获取Driver实现类对象，使用反射
            Class.forName("com.mysql.jdbc.Driver");
        /*相较于方式三，可以省略以下操作
            Driver driver = (Driver) clazz.newInstance();
            DriverManager.registerDriver(driver);
        */
            /**
             *在mysql的Driver实现类中，声明了如下的操作
             *static {
             *      try {
             *          DriverManager.registerDriver(new Driver());
             *      } catch(SqlException e) {
             *          throw new RuntimeException("Cant't register driver!");
             *      }
             * }
             */
            //3、获取连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void testConnection6() {
        //1.加载配置文件
        try(InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            Properties pros = new Properties();
            pros.load(is);
            //2.读取配置信息
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");
            //3.加载驱动
            Class.forName(driverClass);
            //4.获取连接
            Connection conn = DriverManager.getConnection(url,user,password);
            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
