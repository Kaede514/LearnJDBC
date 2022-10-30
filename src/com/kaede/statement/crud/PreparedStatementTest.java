package com.kaede.statement.crud;

import com.kaede.preparedstatement.crud.PreparedStatementQuery;

import java.util.Scanner;

/**
 * @author kaede
 * @create 2022-10-29
 *
 * 演示PreparedStatement替换Statement解决SQL注入问题
 */

public class PreparedStatementTest {

    public static void main(String[] args) {
        new PreparedStatementTest().testLogin();
    }

    public void testLogin() {
        Scanner scan = new Scanner(System.in);
        System.out.print("请输入用户名：");
        String userName = scan.nextLine();
        System.out.print("请输入密码：");
        String password = scan.nextLine();
        // SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '= 1 or '1' = '1';
        // user = 1' or   password = = 1 or '1' = '1
        String sql = "SELECT user,password FROM user_table WHERE user = ? AND password = ?";
        User user = PreparedStatementQuery.getInstance(User.class,sql,userName,password);
        if (user != null) {
            System.out.println("登陆成功!");
        } else {
            System.out.println("用户名或密码错误！");
        }
    }

}
