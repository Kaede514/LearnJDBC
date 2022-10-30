package com.kaede.dbutils;

import com.kaede.been.Customer;
import com.kaede.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * commons-dbutils是Apache组织提供的一个开源JDBC工具类库，封装了针对于数据库的增删改查操作
 */

public class QueryRunnerTest {
    
    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
            String sql = "INSERT INTO customers(name,email,birth) VALUES(?,?,?)";
            int insertCount = runner.update(conn, sql, "nazimi", "nazimi@126.com", "2000-08-24");
            System.out.println("添加了"+insertCount+"条记录");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }
    
    /**
     * BeanHandler是ResultSetHandler接口的实现类，用于封装表中一条记录
     */
    @Test
    public void testQuery1() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
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
     * BeanListHandler是ResultSetHandler接口的实现类，用于封装表中多条记录构成的集合
     */
    @Test
    public void testQuery2() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id > ?";
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);
            List<Customer> list = runner.query(conn, sql, handler, 18);
            list.forEach(x -> System.out.println(x));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * MapHandler是ResultSetHandler接口的实现类，用于封装表中一条记录
     * 将字段和相应字段的值作为map中的key和value
     */
    @Test
    public void testQuery3() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
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
     * MapListHandler是ResultSetHandler接口的实现类，用于封装表中多条记录构成的集合
     * 将字段和相应字段的值作为map中的key和value，将这些map添加到List中
     */
    @Test
    public void testQuery4() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
            String sql = "SELECT id,name,email,birth FROM customers WHERE id > ?";
            MapListHandler handler = new MapListHandler();
            List<Map<String,Object>> list = runner.query(conn, sql, handler, 18);
            list.forEach(x -> System.out.println(x));
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /**
     * ScalarHandler是ResultSetHandler接口的实现类，用于查询特殊值
     */
    @Test
    public void testQuery5() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
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
            conn = JDBCUtils.getConnectionDruid();
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
     * 自定义ResultSetHandler接口的实现类
     */
    @Test
    public void testQuery7() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnectionDruid();
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
            Customer customer = runner.query(conn, sql, handler, 20);
            System.out.println(customer);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

}
