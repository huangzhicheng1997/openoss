package io.oss.util.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhicheng
 * @date 2021-01-22 14:45
 */
public class PlatformUtil {
    public static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.equals("linux");
    }

    public static void main(String[] args) {
        List list=new ArrayList<>();
        list.add(1);

    }
}
