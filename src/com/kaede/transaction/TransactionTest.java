package com.kaede.transaction;

import java.lang.reflect.Field;
import java.sql.*;

import com.kaede.util.JDBCUtils;
import org.junit.jupiter.api.Test;

/**
 * DDL操作一旦执行，都会自动提交
 * DML默认情况下一旦执行，都会自动提交，可以通过 set autocommit = false 的方式取消DML操作的自动提交
 * 默认在连接关闭时，会自动地提交数据
 */
public class TransactionTest {

    @Test
    public void testUpdate() {
        String sql1 = "UPDATE user_table SET balance = balance - 100 WHERE user = ?";
        update(sql1, "AA");
        //模拟网络异常
        System.out.println(10 / 0);
        String sql2 = "UPDATE user_table SET balance = balance + 100 WHERE user = ?";
        update(sql2, "BB");
        System.out.println("转账成功");
    }

    @Test
    public void testUpdateWithTx() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            //取消数据的自动提交
            conn.setAutoCommit(false);
            String sql1 = "UPDATE user_table SET balance = balance - 100 WHERE user = ?";
            update(conn, sql1, "AA");
            //模拟网络异常
            System.out.println(10 / 0);
            String sql2 = "UPDATE user_table SET balance = balance + 100 WHERE user = ?";
            update(conn, sql2, "BB");
            System.out.println("转账成功");
            //提交数据
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //回滚数据
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            //修改其为自动提交数据
            //主要针对于数据库连接池的使用
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, null);
        }
    }

    //通用的增删改操作  --version1.0
    public int update(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1、获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2、预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //3、填充占位符
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //与数据库交互的索引从1开始
            }
            //4、执行
            return ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5、资源的关闭
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;
    }

    //考虑事务后的增删改操作
    //通用的增删改操作  --version2.0
    public int update(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        try {
            //1、预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //2、填充占位符
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //与数据库交互的索引从1开始
            }
            //3、执行
            return ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //4、资源的关闭
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }

    @Test
    public void testQueryWithTx() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            //获取当前连接是否自动提交
            System.out.println("conn.getAutoCommit() = " + conn.getAutoCommit());
            //获取当前连接的隔离级别
            System.out.println("conn.getTransactionIsolation() = " + conn.getTransactionIsolation());
            //取消数据的自动提交
            conn.setAutoCommit(false);
            String sql = "SELECT user,password,balance FROM user_table WHERE user = ?";
            User user = getInstance(conn, User.class, sql, "cc");
            System.out.println(user);
            //提交数据
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //回滚数据
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            //修改其为自动提交数据
            //主要针对于数据库连接池的使用
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, null);
        }
    }

    //考虑事务后的查询操作
    //针对于不同的表的通用的查询操作，返回表中的一条记录  --version2.0
    public static <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            //填充占位符
            for(int i=0; i < args.length; i++) {
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
            JDBCUtils.closeResource(null, ps, rs);
        }
        return null;
    }
}
