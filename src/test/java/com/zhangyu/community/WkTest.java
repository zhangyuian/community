package com.zhangyu.community;

import java.io.IOException;

/**
 * @author: zhang
 * @date: 2022/4/28
 * @description:
 */
public class WkTest {
    public static void main(String[] args) {
        String cmd = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltoimage --quality 75 https://www.nowcoder.com/ d:/2.png";
        try {
            Runtime.getRuntime().exec(cmd); //将命令提交给操作系统的命令行执行，为异步操作
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
