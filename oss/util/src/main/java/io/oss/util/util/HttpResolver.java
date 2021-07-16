package io.oss.util.util;

import io.oss.util.exception.BadRequestException;

/**
 * @Author zhicheng
 * @Date 2021/6/8 4:57 下午
 * @Version 1.0
 */
public class HttpResolver {
    public static long[] resolveRange(String range) {
        try {
            String[] rangeStr = range.split("=")[1].split("-");
            long begin = Long.parseLong(rangeStr[0]);
            long end = Long.parseLong(rangeStr[1]);
            if ((end - begin + 1) > Integer.MAX_VALUE) {
                throw new BadRequestException("range over flow");
            }
            return new long[]{begin, end};
        } catch (Exception e) {
            throw new BadRequestException("bad header 'range'");
        }
    }
}
