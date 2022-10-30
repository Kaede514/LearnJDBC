package com.kaede.blob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.kaede.util.JDBCUtils;
import org.junit.jupiter.api.Test;

/**
 * 使用PrepareStatement实现批量数据的操作
 * 
 * update、delete本身就具有批量操作的效果
 * 
 * 此时的批量操作，主要指批量插入，使用PrepareStatement实现更高效的批量插入
 * 
 * 题目：向goods表中插入20000条数据
 */

public class InsertTest {

    @Test
    public void testInsert1() {
        Connection conn = null;
        Statement st = null;
        try {
            long startTime = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            st = conn.createStatement();
            for(int i=1; i<=20000; i++) {
                String sql = "INSERT INTO goods(name) VALUES('name_" + i + "')";
                st.execute(sql);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("----- cost time: " + (endTime - startTime) + " ms -----");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, st);
        }
    }

    @Test
    public void testInsert2() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long startTime = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1; i<=20000; i++) {
                ps.setObject(1, "name_" + i);
                ps.execute();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("----- cost time: " + (endTime - startTime) + " ms -----");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    /*
     * 修改1：使用addBatch()/executeBatch()/clearBatch()
     * 修改2：mysql服务器默认是关闭批处理的，需要通过一个参数，让mysql开启批处理的支持
     * 		 rewriteBatchedStatements=true 写在配置文件的url后面
     * 修改3：使用更新的mysql驱动：mysql-connector-java-5.1.37-bin.jar，旧版本的不支持批处理
     */
    @Test
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long startTime = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1; i<=20000; i++) {
                ps.setObject(1, "name_" + i);
                //1、积累sql
                ps.addBatch();
                if(i % 500 == 0) {
                    //2、执行batch
                    ps.executeBatch();
                    //3、清空batch
                    ps.clearBatch();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("----- cost time: " + (endTime - startTime) + " ms -----");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //设置不允许自动提交数据
    @Test
    public void testInsert4() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long startTime = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            //设置不允许自动提交数据
            conn.setAutoCommit(false);
            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1; i<=20000; i++) {
                ps.setObject(1, "name_" + i);
                //1、积累sql
                ps.addBatch();
                if(i % 500 == 0) {
                    //2、执行batch
                    ps.executeBatch();
                    //3、清空batch
                    ps.clearBatch();
                }
            }
            //提交数据
            conn.commit();
            long endTime = System.currentTimeMillis();
            System.out.println("----- cost time: " + (endTime - startTime) + " ms -----");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }
}
