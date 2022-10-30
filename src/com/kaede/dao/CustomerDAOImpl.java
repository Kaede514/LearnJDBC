package com.kaede.dao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import com.kaede.been.Customer;

public class CustomerDAOImpl extends BaseDAO implements CustomerDAO {

    @Override
    public void insert(Connection conn, Customer customer) {
        String sql = "INSERT INTO customers(name,email,birth) VALUES(?,?,?)";
        update(conn, sql, customer.getName(), customer.getEmail(), customer.getBirth());        
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        update(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Customer customer) {
        String sql = "UPDATE customers SET name=?,email=?,birth=? WHERE id = ?";
        update(conn, sql, customer.getName(), customer.getEmail(), customer.getBirth(), customer.getId());        
    }

    @Override
    public Customer getCustomerById(Connection conn, int id) {
        String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
        Customer customer = getInstance(conn, Customer.class, sql, id);
        return customer;
    }

    @Override
    public List<Customer> getAll(Connection conn) {
        String sql = "SELECT id,name,email,birth FROM customers";
        List<Customer> list = getForList(conn, Customer.class, sql);
        return list;
    }

    @Override
    public long getCount(Connection conn) {
        String sql = "SELECT COUNT(*) FROM customers";
        return getValue(conn, sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "SELECT MAX(birth) FROM customers";
        return getValue(conn, sql);
    }
    
}
