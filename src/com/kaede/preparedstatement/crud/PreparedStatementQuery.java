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
 * 使用PreparedStatement实现针对于不同表的通用的查询操作
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

    //针对于不同的表的通用的查询操作，返回表中的一条记录
    public <T> T getInstance(Class<T> clazz, String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //填充占位符
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //获取结果的元数据，元数据：描述数据的数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数  
            int columnCount = rsmd.getColumnCount();

            if(rs.next()) {
                T t = clazz.newInstance(); 

                //处理结果集一行数据中的每个列
                for(int i=0; i<columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //给Customer对象指定的columnName属性赋值为columnValue，通过反射
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
        //在这里处理异常而让里面的抛出异常是因为这里一个被抛出了多个异常，在这里发生一个异常
        //直接停止然后统一解决，防止多次try catch检测异常，前一个出现问题下面的就不会执行
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //填充占位符
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //获取结果的元数据，元数据：描述数据的数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数  
            int columnCount = rsmd.getColumnCount();

            //常见集合对象
            ArrayList<T> list = new ArrayList<>();
            while(rs.next()) {
                T t = clazz.newInstance(); 

                //处理结果集一行数据中的每个列：给T对象指定的属性赋值
                for(int i=0; i<columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //给Customer对象指定的columnName属性赋值为columnValue，通过反射
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
