package com.kaede.dao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import com.kaede.been.Customer;

/**
 * �˽ӿ����ڹ淶�����customers��ĳ��ò���
 */

public interface CustomerDAO {
    //��customer������ӵ����ݿ���
    void insert(Connection conn, Customer customer);

    //���ָ����id��ɾ�����е�һ����¼
    void deleteById(Connection conn, int id);

    //����ڴ��е�customer�����޸ı���ָ���ļ�¼
    void update(Connection conn, Customer customer);

    //���ָ����id��ѯ�õ���Ӧ��Customer����
    Customer getCustomerById(Connection conn, int id);

    //��ѯ���е����м�¼���ɵļ���
    List<Customer> getAll(Connection conn);

    //���ر������ݵ���Ŀ��
    long getCount(Connection conn);

    //�������ݱ�����������
    Date getMaxBirth(Connection conn); 
}
