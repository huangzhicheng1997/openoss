package io.oss.util.util;

public class FileUtil {
    public static final String POINT=".";

    public static String getFileSuffix(String fileName){
        return fileName.split("\\.")[1];
    }

    public static String getFileNameWithoutSuffix(String fileName){
        return fileName.split("\\.")[0];
    }
}
