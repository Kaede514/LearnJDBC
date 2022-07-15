package com.kaede.exer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

import com.kaede.util.JDBCUtils;

public class Exer1Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入用户名：");
        String name = scanner.next();
        System.out.print("请输入邮箱：");
        String email = scanner.next();
        System.out.print("请输入生日：");
        //mysql有隐式转换
        String birthday = scanner.next();
        scanner.close();

        String sql = "INSERT INTO customers(name, email, birth) VALUES(?, ?, ?)";
        int insertCount = new Exer1Test().update(sql, name, email, birthday);

        if(insertCount > 0) {
            System.out.println("添加成功");
        } else {
            System.out.println("添加失败");
        }
    }
    
    //通用的增删改操作
    public int update(String sql, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1、获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2、预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //3、填充占位符
            for(int i=0; i< args.length; i++) {
                ps.setObject(i+1, args[i]); //与数据库交互的索引从1开始
            }
            //4、执行
            // return ps.execute();
            //如果执行的是查询操作，有返回结果，则此方法返回true；如果执行的是增删改操作，则此方法返回false
            return ps.executeUpdate();  //返回增删改执行后影响的行数
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //5、资源的关闭
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;
    }
}
