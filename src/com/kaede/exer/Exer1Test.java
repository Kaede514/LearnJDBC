package com.kaede.exer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

import com.kaede.util.JDBCUtils;

public class Exer1Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("�������û�����");
        String name = scanner.next();
        System.out.print("���������䣺");
        String email = scanner.next();
        System.out.print("���������գ�");
        //mysql����ʽת��
        String birthday = scanner.next();
        scanner.close();

        String sql = "INSERT INTO customers(name, email, birth) VALUES(?, ?, ?)";
        int insertCount = new Exer1Test().update(sql, name, email, birthday);

        if(insertCount > 0) {
            System.out.println("��ӳɹ�");
        } else {
            System.out.println("���ʧ��");
        }
    }
    
    //ͨ�õ���ɾ�Ĳ���
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
}
