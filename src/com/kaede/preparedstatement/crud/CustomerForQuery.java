package com.kaede.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;
import org.junit.jupiter.api.Test;

/**
 * 针对于customer表的查询操作
 */

public class CustomerForQuery {
    public static void main(String[] args) throws Exception {
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = new CustomerForQuery().queryForCustomers(sql, 19);
        System.out.println(customer);
        String sql2 = "select id,name,email from customers where name = ?";
        Customer customer2 = new CustomerForQuery().queryForCustomers(sql2, "琴里");
        System.out.println(customer2);
    }

    @Test
    public void testQuery() throws Exception{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql ="select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 19);
            //执行并返回结果集
            rs = ps.executeQuery();
            //处理结果集
            if(rs.next()) { //判断结果集的下一条是否有数据，如果有数据返回true并指针下移，否则指针不会下移直接结束
                //获取当前这条数据的各个字段值
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                Date date = rs.getDate(4);
                //方式1：
                // System.out.println("id = "+id+", name = "+name+", birth = "+email+", date = "+date);
                //方式2：
                // Object[] data = new Object[]{id, name, email, date};
                //方式3：将数据封装为一个对象（推荐）
                Customer customer = new Customer(id, name, email, date);
                System.out.println(customer);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(conn, ps, rs);
        }
    }

    //针对于customers表的通用的查询操作
    public Customer queryForCustomers(String sql, Object ...args){
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
                Customer customer = new Customer();
                //处理结果集一行数据中的每个列
                for(int i=0; i<columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName = rsmd.getColumnName(i + 1);
                    //给Customer对象指定的columnName属性赋值为columnValue，通过反射
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