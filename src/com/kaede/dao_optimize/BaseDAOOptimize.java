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

//优化后的

public abstract class BaseDAOOptimize<T> {

    private Class<T> clazz = null;

    {
        //获取BaseDAOOptimize的子类继承的父类中的泛型
        Type genericSuperClass = this.getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) genericSuperClass;
        Type[] types = paramType.getActualTypeArguments();
        clazz = (Class<T>) types[0];
    }

    //通用的增删改操作  --version2.0（考虑到事务）
    public int update(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        try {
            //1、预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //2、填充占位符
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //与数据库交互的索引从1开始
            }
            //3、执行
            // return ps.execute();
            //如果执行的是查询操作，有返回结果，则此方法返回true；如果执行的是增删改操作，则此方法返回false
            return ps.executeUpdate();  //返回增删改执行后影响的行数
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //4、资源的关闭
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }   

    //针对于不同的表的通用的查询操作，返回表中的一条记录  --version2.0（考虑到事务）
    public T getInstance(Connection conn,   String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //填充占位符
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //获取结果的元数据，元数据：描述数据的数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数  
            int columnCount = rsmd.getColumnCount();

            if(rs.next()) {
                T t = clazz.newInstance(); 

                //处理结果集一行数据中的每个列
                for(int i=0; i<columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //给Customer对象指定的columnName属性赋值为columnValue，通过反射
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

    //针对于不同的表的通用的查询操作，返回表中多条记录构成的集合  --version2.0（考虑到事务）
    public List<T> getForList(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //在这里处理异常而让里面的抛出异常是因为这里一个被抛出了多个异常，在这里发生一个异常
        //直接停止然后统一解决，防止多次try catch检测异常，前一个出现问题下面的就不会执行
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            //填充占位符
            for(int i=0; i<args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            
            //获取结果的元数据，元数据：描述数据的数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数  
            int columnCount = rsmd.getColumnCount();

            //常见集合对象
            ArrayList<T> list = new ArrayList<>();
            while(rs.next()) {
                T t = clazz.newInstance(); 

                //处理结果集一行数据中的每个列：给T对象指定的属性赋值
                for(int i=0; i<columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //给Customer对象指定的columnName属性赋值为columnValue，通过反射
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
