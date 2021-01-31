package io.oss.util.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-01-22 14:45
 */
public class PlatformUtil {
    public static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.equals("linux");
    }
}
