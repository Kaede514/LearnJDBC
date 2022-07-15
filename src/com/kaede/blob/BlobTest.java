package com.kaede.blob;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
    
import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;

/**
 * ����ʹ��PreparedStatement������Bolb��������
 */

public class BlobTest {
    public static void main(String[] args) {
        // new BlobTest().testInsert();
        // new BlobTest().testQuery();
        // new BlobTest().testInsert2();
    }

    //�����ݱ�customers�в���Bolb��������
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream fis = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO customers(name,email,birth,photo) VALUES(?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, "lunar");
            ps.setObject(2, "lunar@126.com");
            ps.setObject(3, "2003-03-27");
            fis = new FileInputStream("lunarsama.jpg");
            ps.setBlob(4, fis);
    
            ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //��ѯ���ݱ�customers�е�Bolb��������
    public void testQuery() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "SELECT id,name,email,birth,photo FROM customers WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 29);
    
            rs = ps.executeQuery();
            if(rs.next()) {
                // ��ʽ1��
                // int id = rs.getInt(1);
                // String name = rs.getString(2);
                // String email = rs.getString(3);
                // Date birth = rs.getDate(4);
                // ��ʽ2��
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");
    
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
    
                //��Blob���ֶ��������������ļ��ķ�ʽ�����ڱ���
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("lunar_copy.jpg");
                byte[] buffer = new byte[1024];
                int temp;
                while((temp = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, temp);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, ps, rs);
        }
    }

    public void testInsert2() {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream fis = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "UPDATE customers SET photo = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            fis = new FileInputStream("D:\\aaaͼƬ\\��Ϊϣ���ľ���_63979045_p4.png");
            ps.setBlob(1, fis);
            ps.setInt(2, 28);
    
            ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, ps);
        }
    }
}
