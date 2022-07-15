package com.kaede.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.kaede.been.Order;
import com.kaede.util.JDBCUtils;

/**
 * 针对于order表通用的查询操作
 */

 /**
  * 针对表的字段名与类的属性名不相同的情况
  *1、必须声明sql，使用类的属性名来命名字段的别名
  *2、使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName()，获取类的别名，未起别名则会获取类名
  */
public class OrderForQuery {
    public static void main(String[] args) {
        //列名与属性不匹配
        // String sql = "select order_id,order_name,order_date from `order` where order_id = ?";
        
        //getColumnName()获取的是列名，不是别名，故需使用getColumnLabel()
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
                    //获取列的列名，getColumnName   --不推荐使用
                    // String columnName = rsmd.getColumnName(i + 1);

                    //获取列的别名，getColumnLabel  --未起别名的话默认为类名
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