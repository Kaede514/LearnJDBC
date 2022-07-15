package com.kaede.preparedstatement.crud;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import com.kaede.util.JDBCUtils;

/**
 * ʹ��PreparedStatement���滻Statement��ʵ�ֶ����ݱ����ɾ�Ĳ����
 * ��ɾ�ģ���
 * 
 * �ô���
 * 1��PreparedStatement�滻Statement�����ƴ�����µ�sqlע������
 * 2��PreparedStatement���Բ���Blob���ݣ���Statement������
 * 3��PreparedStatement����ʵ�ָ���Ч����������
 */

public class PreparedStatementUpdateTest {
    public static void main(String[] args){
        // new PreparedStatementUpdateTest().testInsert();
        // new PreparedStatementUpdateTest().testUpdate();
        // new PreparedStatementUpdateTest().update("delete from customers where id = ?", 3);
        //mysql�ؼ���Ҫ�����غţ�����ᱨ��
        // String sql = "update order set order_name = ? where order_id = ?";
        String sql = "update `order` set order_name = ? where order_id = ?";
        new PreparedStatementUpdateTest().update(sql, "DD", 2);
    }

    //��customer�������һ����¼
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream fis = null;
        //���ﲻ��try with-resources����ΪConnection��PreparedStatement�ǽӿڣ�ֻ��ʵ����AutoCloseable�ӿ�
        //���ࣨʵ����close()�������ſ���ʹ��try with- resources
        try {
            //1����ȡ�����ļ��е��ĸ�������Ϣ   
            fis = new FileInputStream("C:\\Users\\hufeng\\code\\JDBC\\src\\com\\kaede\\connection\\jdbc.properties");
            
            Properties pros = new Properties();
            pros.load(fis);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            //2����������
            Class.forName(driverClass);

            //3����ȡ����
            conn = DriverManager.getConnection(url, user, password);
            
            //4��Ԥ����sql��䣬����PreparedStatementʵ��
            String sql = "insert into customers(name,email,birth) values(?,?,?)";
            ps = conn.prepareStatement(sql);

            //5�����ռλ��
            ps.setString(1,"����");
            ps.setString(2, "kotory@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("2001-08-03");
            ps.setDate(3, new java.sql.Date(date.getTime()));

            //6��ִ�в���
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //7����Դ�Ĺر�
            try {
                if(fis != null)
                    fis.close();
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

    //�޸�customer���е�һ����¼
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1����ȡ���ݿ������
            conn = JDBCUtils.getConnection();

            //2��Ԥ����sql��䣬����PreparedStatement��ʵ��
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);

            //3�����ռλ��
            ps.setObject(1, "Ī����");
            ps.setObject(2, 18);

            //4��ִ��
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5����Դ�Ĺر�
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //ͨ�õ���ɾ�Ĳ���
    public void update(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1����ȡ���ݿ������
            conn = JDBCUtils.getConnection();
            //2��Ԥ����sql��䣬����PreparedStatement��ʵ��
            ps = conn.prepareStatement(sql);
            //3�����ռλ��
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //�����ݿ⽻����������1��ʼ
            }
            //4��ִ��
            ps.execute();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5����Դ�Ĺر�
            JDBCUtils.closeResource(conn, ps);
        }   
    }
}
