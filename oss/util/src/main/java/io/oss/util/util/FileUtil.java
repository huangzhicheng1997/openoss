package io.oss.util.util;

import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String POINT = ".";
    public static final String TEMP_FILE_FLAG = "#";

    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getFileNameWithoutSuffix(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getOriginNameForTempFile(String tempFileName) {
        return tempFileName.substring(0, tempFileName.lastIndexOf("#"));
    }

    public static String getFileNumber(String fileName) {
        String fileNameWithoutSuffix = getFileNameWithoutSuffix(fileName);
        char[] chars = fileNameWithoutSuffix.toCharArray();

        boolean start = false;
        List<Character> numbers = new ArrayList<>();
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == '(') {
                break;
            }
            if (start) {
                numbers.add(chars[i]);
            }
            if (chars[i] == ')') {
                start = true;
            }
        }

        StringBuilder number = new StringBuilder();
        numbers.forEach(number::append);
        return number.reverse().toString();
    }
}
