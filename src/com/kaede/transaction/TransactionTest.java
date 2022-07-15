package com.kaede.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kaede.util.JDBCUtils;

/**
 * DDL����һ��ִ�У������Զ��ύ
 * DMLĬ�������һ��ִ�У������Զ��ύ������ͨ��set autocommit = false�ķ�ʽȡ��DML�������Զ��ύ
 * Ĭ�������ӹر�ʱ�����Զ����ύ����
 */
public class TransactionTest {
    /**
     * ��������ݱ�user_table��AA�û���BB�û�ת��100
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

        //ģ�������쳣
        System.out.println(10 / 0);

        String sql2 = "UPDATE user_table SET balance = balance + 100 WHERE user = ?";
        update(sql2, "BB");

        System.out.println("ת�˳ɹ�");
    }

    public void testUpdateWithTx() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            System.out.println(conn.getAutoCommit());

            //  ȡ�����ݵ��Զ��ύ
            conn.setAutoCommit(false);

            String sql1 = "UPDATE user_table SET balance = balance - 100 WHERE user = ?";
            update(conn, sql1, "AA");
    
            //ģ�������쳣
            System.out.println(10 / 0);
    
            String sql2 = "UPDATE user_table SET balance = balance + 100 WHERE user = ?";
            update(conn, sql2, "BB");
    
            System.out.println("ת�˳ɹ�");

            //�ύ����
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //�ع�����
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            //�޸���Ϊ�Զ��ύ����
            //��Ҫ��������ݿ����ӳص�ʹ��
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, null);
        }
    }

    //ͨ�õ���ɾ�Ĳ���  --version1.0
    public int update(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1����ȡ���ݿ������
            conn = JDBCUtils.getConnection();
            //2��Ԥ����sql��䣬����PreparedStatement��ʵ��
            ps = conn.prepareStatement(sql);
            //3�����ռλ��
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //�����ݿ⽻����������1��ʼ
            }
            //4��ִ��
            // return ps.execute();
            //���ִ�е��ǲ�ѯ�������з��ؽ������˷�������true�����ִ�е�����ɾ�Ĳ�������˷�������false
            return ps.executeUpdate();  //������ɾ��ִ�к�Ӱ�������
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5����Դ�Ĺر�
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;
    }

    //������������ɾ�Ĳ���
    //ͨ�õ���ɾ�Ĳ���  --version2.0
    public int update(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        try {
            //1��Ԥ����sql��䣬����PreparedStatement��ʵ��
            ps = conn.prepareStatement(sql);
            //2�����ռλ��
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //�����ݿ⽻����������1��ʼ
            }
            //3��ִ��
            // return ps.execute();
            //���ִ�е��ǲ�ѯ�������з��ؽ������˷�������true�����ִ�е�����ɾ�Ĳ�������˷�������false
            return ps.executeUpdate();  //������ɾ��ִ�к�Ӱ�������
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //4����Դ�Ĺر�
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }   
}
