package com.kaede.dao;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;
import org.junit.jupiter.api.Test;

public class CustomerDAOImplTest {

    private CustomerDAOImpl dao = new CustomerDAOImpl();

    @Test
    public void testDeleteById() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();   
            dao.deleteById(conn, 4);
            System.out.println("删除成功");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testGetAll() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();   
            List<Customer> customer = dao.getAll(conn);
            customer.forEach(x -> System.out.println(x));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testGetCount() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();   
            long record = dao.getCount(conn);
            System.out.println("记录数为：" + record);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testGetCustomerById() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();   
            Customer customer = dao.getCustomerById(conn, 19);
            System.out.println(customer);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testGetMaxBirth() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();   
            Date date = dao.getMaxBirth(conn);
            System.out.println(date);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("2001-08-03");
            Customer customer = new Customer("solar", "solar@126.com", new Date(date.getTime()));
            dao.insert(conn, customer);
            System.out.println("添加成功");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testUpdate() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("2014-01-17");
            Customer customer = new Customer(18, "莫扎特", "mozhate@126.com", new Date(date.getTime()));
            dao.update(conn, customer);
            System.out.println("修改成功");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

}
