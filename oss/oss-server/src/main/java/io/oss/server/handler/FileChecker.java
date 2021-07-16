package io.oss.server.handler;

import io.netty.util.internal.StringUtil;
import io.oss.systemcall.SystemCall;
import io.oss.util.util.FileUtil;

import java.util.Arrays;

/**
 * @Author zhicheng
 * @Date 2021/6/24 7:30 下午
 * @Version 1.0
 */
public class FileChecker {

    // 声明图片后缀名数组
    private static String[] imgArray = new String[]{"bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico"};

    /**
     * 查看文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileIsExist(String filePath) {
        return 0 == SystemCall.INSTANCE.access(filePath, SystemCall.F_OK);
    }

    /**
     * 文件是否为图片
     *
     * @param filePath
     * @return
     */
    public static boolean isImage(String filePath) {
        if (StringUtil.isNullOrEmpty(filePath)) {
            return false;
        }
        String fileSuffix = FileUtil.getFileSuffix(filePath);
        return Arrays.asList(imgArray).contains(fileSuffix);
    }

}
