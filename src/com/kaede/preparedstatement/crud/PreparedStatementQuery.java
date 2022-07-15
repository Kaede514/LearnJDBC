package com.kaede.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.kaede.been.Customer;
import com.kaede.been.Order;
import com.kaede.util.JDBCUtils;

/**
 * ʹ��PreparedStatementʵ������ڲ�ͬ���ͨ�õĲ�ѯ����
 */
public class PreparedStatementQuery {
    public static void main(String[] args) {
        /* 
        String sql = "SELECT id,name,email FROM customers WHERE id = ?";
        Customer customer = new PreparedStatementQuery().getInstance(Customer.class, sql, 19);
        System.out.println(customer);

        String sql2 = "SELECT order_id orderId, order_name orderName FROM `order` WHERE order_id = ?";
        Order order = new PreparedStatementQuery().getInstance(Order.class, sql2, 2);
        System.out.println(order);
        */

        String sql = "SELECT id,name,email FROM customers WHERE id <= ?";
        List<Customer> customer = new PreparedStatementQuery().getForList(Customer.class, sql, 19);
        customer.forEach(x -> System.out.println(x));

        String sql2 = "SELECT order_id orderId, order_name orderName FROM `order` WHERE order_id <= ?";
        List<Order> order = new PreparedStatementQuery().getForList(Order.class, sql2, 2);
        order.forEach(System.out::println);

        String sql3 = "SELECT order_id orderId, order_name orderName FROM `order`";
        List<Order> order3 = new PreparedStatementQuery().getForList(Order.class, sql3);
        order3.forEach(System.out::println);
    }

    //����ڲ�ͬ�ı��ͨ�õĲ�ѯ���������ر��е�һ����¼
    public <T> T getInstance(Class<T> clazz, String sql, Object ...args){
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
                T t = clazz.newInstance(); 

                //��������һ�������е�ÿ����
                for(int i=0; i<columnCount; i++) {
                    //��ȡ��ֵ
                    Object columnValue = rs.getObject(i + 1);

                    //��ȡÿ���е�����
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //��Customer����ָ����columnName���Ը�ֵΪcolumnValue��ͨ������
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }

                return t;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }

    public <T> List<T> getForList(Class<T> clazz, String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //�����ﴦ���쳣����������׳��쳣����Ϊ����һ�����׳��˶���쳣�������﷢��һ���쳣
        //ֱ��ֹͣȻ��ͳһ�������ֹ���try catch����쳣��ǰһ��������������ľͲ���ִ��
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

            //�������϶���
            ArrayList<T> list = new ArrayList<>();
            while(rs.next()) {
                T t = clazz.newInstance(); 

                //��������һ�������е�ÿ���У���T����ָ�������Ը�ֵ
                for(int i=0; i<columnCount; i++) {
                    //��ȡ��ֵ
                    Object columnValue = rs.getObject(i + 1);

                    //��ȡÿ���е�����
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //��Customer����ָ����columnName���Ը�ֵΪcolumnValue��ͨ������
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }

            return list;
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    } 
}
