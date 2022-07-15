package com.kaede.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kaede.util.JDBCUtils;

/**
 * DDL操作一旦执行，都会自动提交
 * DML默认情况下一旦执行，都会自动提交，可以通过set autocommit = false的方式取消DML操作的自动提交
 * 默认在连接关闭时，会自动地提交数据
 */
public class TransactionTest {
    /**
     * 针对于数据表user_table，AA用户给BB用户转账100
     * 
     * UPDATE user_table SET balance = balance - 100 WHERE user = 'AA'
     * UPDATE user_table SET balance = balance + 100 WHERE user = 'BB'
     */
    public static void main(String[] args) {
        // new TransactionTest().testUpdate();
        new TransactionTest().testUpdateWithTx();
    }

    public void testUpdate() {
        String sql1 = "UPDATE user_table SET balance = balance - 100 WHERE user = ?";
        update(sql1, "AA");

        //模拟网络异常
        System.out.println(10 / 0);

        String sql2 = "UPDATE user_table SET balance = balance + 100 WHERE user = ?";
        update(sql2, "BB");

        System.out.println("转账成功");
    }

    public void testUpdateWithTx() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            System.out.println(conn.getAutoCommit());

            //  取消数据的自动提交
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
            // return ps.execute();
            //如果执行的是查询操作，有返回结果，则此方法返回true；如果执行的是增删改操作，则此方法返回false
            return ps.executeUpdate();  //返回增删改执行后影响的行数
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
            // return ps.execute();
            //如果执行的是查询操作，有返回结果，则此方法返回true；如果执行的是增删改操作，则此方法返回false
            return ps.executeUpdate();  //返回增删改执行后影响的行数
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //4、资源的关闭
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }   
}
