package com.kaede.blob;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.kaede.util.JDBCUtils;

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
    public static void main(String[] args) {
        // new InsertTest().testInsert1();
        // new InsertTest().testInsert2();
        // new InsertTest().testInsert3();
    }

    public void testInsert1() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
    
            for(int i=1; i<=20000; i++) {
                ps.setObject(1, "name_" + i);
                ps.execute();
            }

            long end = System.currentTimeMillis();
            System.out.println("花费的时间为："+(end-start));   //20000时：25009
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //使用addBatch()、excuteBatch()
    //mysql服务器默认是关闭批处理的，需要通过一个参数让mysql开启批处理的支持
    // ?rewriteBatchedStatements=true 写在配置文件的url后面
    public void testInsert2() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
    
            for(int i=1; i<=100001; i++) {
                ps.setObject(1, "name_" + i);
                
                //1、“攒”sql
                ps.addBatch();

                if(i % 500 == 0) {
                    //2、执行batch
                    ps.executeBatch();

                    //3、清空batch
                    ps.clearBatch();
                }

                //处理剩余的
                if(i == 100001) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            long end = System.currentTimeMillis(); 
            System.out.println("花费的时间为："+(end-start));   //20000时：487，100000时：1160
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //设置不允许自动提交数据
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            //设置不允许自动提交数据
            conn.setAutoCommit(false);

            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
    
            for(int i=1; i<=100001; i++) {
                ps.setObject(1, "name_" + i);
                
                //1、“攒”sql
                ps.addBatch();

                if(i % 500 == 0) {
                    //2、执行batch
                    ps.executeBatch();

                    //3、清空batch
                    ps.clearBatch();
                }

                //处理剩余的
                if(i == 100001) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            //提交数据
            conn.commit();

            long end = System.currentTimeMillis(); 
            System.out.println("花费的时间为："+(end-start));   //20000时：450，100000时：864
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }
}
