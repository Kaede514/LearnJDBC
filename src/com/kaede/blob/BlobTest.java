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
import org.junit.jupiter.api.Test;

/**
 * 测试使用PreparedStatement来操作Bolb类型数据
 */

public class BlobTest {

    //向数据表customers中插入Bolb类型数据
    @Test
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
            //lunasama.jpg放在src下
            fis = new FileInputStream("lunasama.jpg");
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

    //向数据表customers中修改Bolb类型数据
    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream fis = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "UPDATE customers SET photo = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            fis = new FileInputStream("lunasama1.jpg");
            ps.setBlob(1, fis);
            ps.setObject(2,20);
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

    //查询数据表customers中的Bolb类型数据
    @Test
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
            ps.setInt(1,20);
            rs = ps.executeQuery();
            if(rs.next()) {
                // 方式1：
                // int id = rs.getInt(1);
                // String name = rs.getString(2);
                // String email = rs.getString(3);
                // Date birth = rs.getDate(4);
                // 方式2：
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
                //将Blob的字段下载下来，以文件的方式保存在本地
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("lunasama_copy.jpg");
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
}
