package com.kaede.connection;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {
    public static void main(String[] args) throws Exception {
        // new ConnectionTest().testConnection1();
        // new ConnectionTest().testConnection2();
        // new ConnectionTest().testConnection3();
        // new ConnectionTest().testConnection4();
        new ConnectionTest().testConnection5();
    }

    //方式一：
    public void testConnection1() throws SQLException {
        //获取Driver实现类对象
        Driver driver = new com.mysql.jdbc.Driver();

        //jdbc:mysql:协议
        //localhost:ip地址
        //3306:默认mysql的端口号
        //test:test数据库
        String url = "jdbc:mysql://localhost:3306/test";
        //将用户名和密码封装在Propertites中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection conn = driver.connect(url, info);

        System.out.println(conn);
    }

    //方式二：对方式一的迭代：在如下的程序中不出现第三方的API，使得程序具有更好的可移植性
    public void testConnection2() throws Exception {
        //1、获取Driver实现类对象，使用反射
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2、提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/test";
        
        //3、提供连接需要的用户名和密码
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        //4、获取连接
        Connection conn = driver.connect(url, info);
        System.out.println(conn);
    }

    //方式3：使用DriverManager替换Driver
    public void testConnection3() throws Exception {
        //1、获取Driver实现类对象，使用反射
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2、提供另外三个基本连接的信息
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "123456";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

    //方式4：可以只是加载驱动，不用显示地注册驱动
    public void testConnection4() throws Exception {
        //1、提供另外三个基本连接的信息
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "123456";
        
        //2、获取Driver实现类对象，使用反射
        Class.forName("com.mysql.jdbc.Driver");
        //相较于方式三，可以省略以下操作
        // Driver driver = (Driver) clazz.newInstance();
        // //注册驱动
        // DriverManager.registerDriver(driver);
        /**
         * 在mysql的Driver实现类中，声明了如下的操作
         *static {
         *      try {
         *          java.sql.DriverManager.registerDriver(new Driver());
         *      } catch(SqlException e) {
         *          throw new RuntimeException("Cant't register driver!"); 
         *      }
         * }
         */

        //3、获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

    //方式5：将数据库连接需要的四个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    //1、实现了数据与代码的分离，实现了解耦
    //2、如果需要修改配置文件信息，可以避免程序重新打包
    public void testConnection5() throws Exception {
        //1、读取配置文件中的四个基本信息   
        // System.out.println(System.getProperty("user.dir"));

        FileInputStream fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\jdbc.properties");
        // 或者
        //FileInputStream fis = new FileInputStream(".\\src\\com\\kaede\\connection\\jdbc.properties");
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
        System.out.println(conn);
    }
}
