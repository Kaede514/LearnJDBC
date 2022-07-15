package com.kaede.dbutils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

/**
 * commons-dbutils��Apache��֯�ṩ��һ����ԴJDBC������⣬��װ����������ݿ����ɾ�Ĳ����
 */

public class QueryRunnerTest {
    
    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "INSERT INTO customers(name,email,birth) VALUES(?,?,?)";
            int insertCount = runner.update(conn, sql, "nazimi", "nazimi@126.com", "2000-08-24");
            System.out.println("�����"+insertCount+"����¼");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }
    
    /**
     * BeanHandler��ResultSetHandler�ӿڵ�ʵ���࣬���ڷ�װ����һ����¼
     */
    @Test
    public void testQuery1() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id = ?";
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);
            Customer customer = runner.query(conn, sql, handler, 19);
            System.out.println(customer);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * BeanListHandler��ResultSetHandler�ӿڵ�ʵ���࣬���ڷ�װ���ж�����¼���ɵļ���
     */
    @Test
    public void testQuery2() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id < ?";
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);
            List<Customer> list = runner.query(conn, sql, handler, 30);
            list.forEach(x -> System.out.println(x));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * MapHandler��ResultSetHandler�ӿڵ�ʵ���࣬���ڷ�װ����һ����¼
     * ���ֶκ���Ӧ�ֶε�ֵ��Ϊmap�е�key��value
     */
    @Test
    public void testQuery3() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id = ?";
            MapHandler handler = new MapHandler();
            Map<String,Object> map = runner.query(conn, sql, handler, 19);
            System.out.println(map);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * MapListHandler��ResultSetHandler�ӿڵ�ʵ���࣬���ڷ�װ���ж�����¼���ɵļ���
     * ���ֶκ���Ӧ�ֶε�ֵ��Ϊmap�е�key��value������Щmap��ӵ�List��
     */
    @Test
    public void testQuery4() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id < ?";
            MapListHandler handler = new MapListHandler();
            List<Map<String,Object>> list = runner.query(conn, sql, handler, 30);
            list.forEach(x -> System.out.println(x));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * ScalarHandler��ResultSetHandler�ӿڵ�ʵ���࣬���ڲ�ѯ����ֵ
     */
    @Test
    public void testQuery5() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT COUNT(*) FROM customers";
            ScalarHandler handler = new ScalarHandler();
            long count = (long) runner.query(conn, sql, handler);
            System.out.println(count);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    @Test
    public void testQuery6() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT MAX(birth) FROM customers";
            ScalarHandler handler = new ScalarHandler();
            Date date = (Date) runner.query(conn, sql, handler);
            System.out.println(date);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * �Զ���ResultSetHandler�ӿڵ�ʵ����
     */
    @Test
    public void testQuery7() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection1();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id = ?";
            ResultSetHandler<Customer> handler = new ResultSetHandler<Customer>() {
                @Override
                public Customer handle(ResultSet rs) throws SQLException {
                    if(rs.next()) { 
                        int id = rs.getInt(1);
                        String name = rs.getString(2);
                        String email = rs.getString(3);
                        Date date = rs.getDate(4);
                        Customer customer = new Customer(id, name, email, date);
                        return customer;
                    }
                    return null;
                } 
            };
            Customer customer = runner.query(conn, sql, handler, 29);
            System.out.println(customer);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }
}
