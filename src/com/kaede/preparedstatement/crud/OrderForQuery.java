package com.kaede.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.kaede.been.Order;
import com.kaede.util.JDBCUtils;

/**
 * �����order��ͨ�õĲ�ѯ����
 */

 /**
  * ��Ա���ֶ������������������ͬ�����
  *1����������sql��ʹ������������������ֶεı���
  *2��ʹ��ResultSetMetaDataʱ����Ҫʹ��getColumnLabel()���滻getColumnName()����ȡ��ı�����δ���������ȡ����
  */
public class OrderForQuery {
    public static void main(String[] args) {
        //���������Բ�ƥ��
        // String sql = "select order_id,order_name,order_date from `order` where order_id = ?";
        
        //getColumnName()��ȡ�������������Ǳ���������ʹ��getColumnLabel()
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = new OrderForQuery().queryForOrder(sql, 1);
        System.out.println(order);
    }

    public Order queryForOrder(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            if(rs.next()) {
                Order order = new Order();

                for(int i=0; i<columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    //��ȡ�е�������getColumnName   --���Ƽ�ʹ��
                    // String columnName = rsmd.getColumnName(i + 1);

                    //��ȡ�еı�����getColumnLabel  --δ������Ļ�Ĭ��Ϊ����
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order, columnValue);
                }

                return order;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }
}