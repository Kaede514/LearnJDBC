package com.kaede.dao_optimize;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.kaede.util.JDBCUtils;

//�Ż����

public abstract class BaseDAOOptimize<T> {

    private Class<T> clazz = null;

    {
        //��ȡBaseDAOOptimize������̳еĸ����еķ���
        Type genericSuperClass = this.getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) genericSuperClass;
        Type[] types = paramType.getActualTypeArguments();
        clazz = (Class<T>) types[0];
    }

    //ͨ�õ���ɾ�Ĳ���  --version2.0�����ǵ�����
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

    //����ڲ�ͬ�ı��ͨ�õĲ�ѯ���������ر��е�һ����¼  --version2.0�����ǵ�����
    public T getInstance(Connection conn,   String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //���ռλ��
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //��ȡ�����Ԫ���ݣ�Ԫ���ݣ��������ݵ�����
            ResultSetMetaData rsmd = rs.getMetaData();
            //ͨ��ResultSetMetaData��ȡ������е�����  
            int columnCount = rsmd.getColumnCount();

            if(rs.next()) {
                T t = clazz.newInstance(); 

                //��������һ�������е�ÿ����
                for(int i=0; i<columnCount; i++) {
                    //��ȡ��ֵ
                    Object columnValue = rs.getObject(i + 1);

                    //��ȡÿ���е�����
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //��Customer����ָ����columnName���Ը�ֵΪcolumnValue��ͨ������
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }

                return t;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    }

    //����ڲ�ͬ�ı��ͨ�õĲ�ѯ���������ر��ж�����¼���ɵļ���  --version2.0�����ǵ�����
    public List<T> getForList(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //�����ﴦ���쳣����������׳��쳣����Ϊ����һ�����׳��˶���쳣�������﷢��һ���쳣
        //ֱ��ֹͣȻ��ͳһ�������ֹ���try catch����쳣��ǰһ��������������ľͲ���ִ��
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //���ռλ��
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //��ȡ�����Ԫ���ݣ�Ԫ���ݣ��������ݵ�����
            ResultSetMetaData rsmd = rs.getMetaData();
            //ͨ��ResultSetMetaData��ȡ������е�����  
            int columnCount = rsmd.getColumnCount();

            //�������϶���
            ArrayList<T> list = new ArrayList<>();
            while(rs.next()) {
                T t = clazz.newInstance(); 

                //��������һ�������е�ÿ���У���T����ָ�������Ը�ֵ
                for(int i=0; i<columnCount; i++) {
                    //��ȡ��ֵ
                    Object columnValue = rs.getObject(i + 1);

                    //��ȡÿ���е�����
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //��Customer����ָ����columnName���Ը�ֵΪcolumnValue��ͨ������
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }

            return list;
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    } 

    public <E> E getValue(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            if(rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    }  

}
