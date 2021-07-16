package io.oss.remoting.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @Author zhicheng
 * @Date 2021/6/24 8:15 下午
 * @Version 1.0
 */
public class MainProcessor {
    public static void main(String[] args) {
        System.out.println("HELLO OPEN OSS........................");
        Scanner scanner = new Scanner(System.in);


        System.out.println("请输入服务器连接地址");
        String url = scanner.next();


        System.out.println("connect to server......");

        System.out.println("login ok!");


        String command = scanner.next();


        while (true) {
            switch (command) {
                case "select":
                    System.out.println("what you want?");
                    command = scanner.next();
                    break;
                case "login":
                    System.out.println("userName:");
                    String userName = scanner.next();
                    System.out.println("password");
                    String password = scanner.next();
                    if (check(userName, password)) {
                        command = "select";
                    }
                    System.out.println("用户名密码不正确");
                    break;
                default:
                    System.out.println("command :" + command + " not find!");
                    command = "select";

            }
        }
    }

    private static boolean check(String userName, String password) {

        return false;
    }

    static class ClientContext {
        public static final String END_CAUSE = "endCause";

        public static Map<String, Object> attr = new HashMap<>();

        public static void add(String key, Object value) {
            attr.put(key, value);
        }

        public static Object get(String key) {
            return attr.get(key);
        }
    }
}
