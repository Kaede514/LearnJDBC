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

    //��ʽһ��
    public void testConnection1() throws SQLException {
        //��ȡDriverʵ�������
        Driver driver = new com.mysql.jdbc.Driver();

        //jdbc:mysql:Э��
        //localhost:ip��ַ
        //3306:Ĭ��mysql�Ķ˿ں�
        //test:test���ݿ�
        String url = "jdbc:mysql://localhost:3306/test";
        //���û����������װ��Propertites��
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection conn = driver.connect(url, info);

        System.out.println(conn);
    }

    //��ʽ�����Է�ʽһ�ĵ����������µĳ����в����ֵ�������API��ʹ�ó�����и��õĿ���ֲ��
    public void testConnection2() throws Exception {
        //1����ȡDriverʵ�������ʹ�÷���
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2���ṩҪ���ӵ����ݿ�
        String url = "jdbc:mysql://localhost:3306/test";
        
        //3���ṩ������Ҫ���û���������
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        //4����ȡ����
        Connection conn = driver.connect(url, info);
        System.out.println(conn);
    }

    //��ʽ3��ʹ��DriverManager�滻Driver
    public void testConnection3() throws Exception {
        //1����ȡDriverʵ�������ʹ�÷���
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2���ṩ���������������ӵ���Ϣ
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "123456";

        //ע������
        DriverManager.registerDriver(driver);

        //��ȡ����
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

    //��ʽ4������ֻ�Ǽ���������������ʾ��ע������
    public void testConnection4() throws Exception {
        //1���ṩ���������������ӵ���Ϣ
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "123456";
        
        //2����ȡDriverʵ�������ʹ�÷���
        Class.forName("com.mysql.jdbc.Driver");
        //����ڷ�ʽ��������ʡ�����²���
        // Driver driver = (Driver) clazz.newInstance();
        // //ע������
        // DriverManager.registerDriver(driver);
        /**
         * ��mysql��Driverʵ�����У����������µĲ���
         *static {
         *      try {
         *          java.sql.DriverManager.registerDriver(new Driver());
         *      } catch(SqlException e) {
         *          throw new RuntimeException("Cant't register driver!"); 
         *      }
         * }
         */

        //3����ȡ����
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

    //��ʽ5�������ݿ�������Ҫ���ĸ�������Ϣ�����������ļ��У�ͨ����ȡ�����ļ��ķ�ʽ����ȡ����
    //1��ʵ�������������ķ��룬ʵ���˽���
    //2�������Ҫ�޸������ļ���Ϣ�����Ա���������´��
    public void testConnection5() throws Exception {
        //1����ȡ�����ļ��е��ĸ�������Ϣ   
        // System.out.println(System.getProperty("user.dir"));

        FileInputStream fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\jdbc.properties");
        // ����
        //FileInputStream fis = new FileInputStream(".\\src\\com\\kaede\\connection\\jdbc.properties");
        Properties pros = new Properties();
        pros.load(fis);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2����������
        Class.forName(driverClass);

        //3����ȡ����
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }
}
