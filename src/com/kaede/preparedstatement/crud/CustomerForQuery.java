package com.kaede.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;

/**
 * �����customer��Ĳ�ѯ����
 */

public class CustomerForQuery {
    public static void main(String[] args) throws Exception {
        // new CustomerForQuery().testQuery1();
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = new CustomerForQuery().queryForCustomers(sql, 19);
        System.out.println(customer);

        String sql2 = "select id,name,email from customers where name = ?";
        Customer customer2 = new CustomerForQuery().queryForCustomers(sql2, "����");
        System.out.println(customer2);
    }
    
    public void testQuery1() throws Exception{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql ="select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 19);

            //ִ�в����ؽ����
            rs = ps.executeQuery();

            //��������
            if(rs.next()) { //�жϽ��������һ���Ƿ������ݣ���������ݷ���true��ָ�����ƣ�����ָ�벻������ֱ�ӽ���
                //��ȡ��ǰ�������ݵĸ����ֶ�ֵ
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                Date date = rs.getDate(4);

                //��ʽ1��
                // System.out.println("id = "+id+", name = "+name+", birth = "+email+", date = "+date);

                //��ʽ2��
                // Object[] data = new Object[]{id, name, email, date};

                //��ʽ3�������ݷ�װΪһ�������Ƽ���
                Customer customer = new Customer(id, name, email, date);
                System.out.println(customer);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //�ر���Դ
            JDBCUtils.closeResource(conn, ps, rs);
        }
    }

    //�����customers���ͨ�õĲ�ѯ����
    public Customer queryForCustomers(String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //���ռλ��
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //��ȡ�����Ԫ���ݣ�Ԫ���ݣ��������ݵ�����
            ResultSetMetaData rsmd = rs.getMetaData();
            //ͨ��ResultSetMetaData��ȡ������е�����  
            int columnCount = rsmd.getColumnCount();

            if(rs.next()) {
                Customer customer = new Customer();

                //��������һ�������е�ÿ����
                for(int i=0; i<columnCount; i++) {
                    //��ȡ��ֵ
                    Object columnValue = rs.getObject(i + 1);

                    //��ȡÿ���е�����
                    String columnName = rsmd.getColumnName(i + 1);

                    //��Customer����ָ����columnName���Ը�ֵΪcolumnValue��ͨ������
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(customer, columnValue);
                }

                return customer;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }
}