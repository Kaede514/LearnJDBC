package com.kaede.blob;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.kaede.util.JDBCUtils;

/**
 * ʹ��PrepareStatementʵ���������ݵĲ���
 * 
 * update��delete����;�������������Ч��
 * 
 * ��ʱ��������������Ҫָ�������룬ʹ��PrepareStatementʵ�ָ���Ч����������
 * 
 * ��Ŀ����goods���в���20000������
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
            System.out.println("���ѵ�ʱ��Ϊ��"+(end-start));   //20000ʱ��25009
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //ʹ��addBatch()��excuteBatch()
    //mysql������Ĭ���ǹر�������ģ���Ҫͨ��һ��������mysql�����������֧��
    // ?rewriteBatchedStatements=true д�������ļ���url����
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
                
                //1�����ܡ�sql
                ps.addBatch();

                if(i % 500 == 0) {
                    //2��ִ��batch
                    ps.executeBatch();

                    //3�����batch
                    ps.clearBatch();
                }

                //����ʣ���
                if(i == 100001) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            long end = System.currentTimeMillis(); 
            System.out.println("���ѵ�ʱ��Ϊ��"+(end-start));   //20000ʱ��487��100000ʱ��1160
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //���ò������Զ��ύ����
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            //���ò������Զ��ύ����
            conn.setAutoCommit(false);

            String sql = "INSERT INTO goods(name) VALUES(?)";
            ps = conn.prepareStatement(sql);
    
            for(int i=1; i<=100001; i++) {
                ps.setObject(1, "name_" + i);
                
                //1�����ܡ�sql
                ps.addBatch();

                if(i % 500 == 0) {
                    //2��ִ��batch
                    ps.executeBatch();

                    //3�����batch
                    ps.clearBatch();
                }

                //����ʣ���
                if(i == 100001) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            //�ύ����
            conn.commit();

            long end = System.currentTimeMillis(); 
            System.out.println("���ѵ�ʱ��Ϊ��"+(end-start));   //20000ʱ��450��100000ʱ��864
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }
}
