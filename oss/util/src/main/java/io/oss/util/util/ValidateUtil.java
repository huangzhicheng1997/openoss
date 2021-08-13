package io.oss.util.util;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author zhicheng
 * @date 2021-07-17 16:02
 */
public class ValidateUtil {
    public static boolean isBlank(String... args) {
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                return false;
            }
        }
        return true;
    }

    public static void assertStringNotBlank(String... args) {
        for (String arg : args) {
            assert StringUtils.isNotBlank(arg);
        }
    }

    public static void assertNotBlankEmptyAndNull(Object... args) {
        for (Object arg : args) {
            assert !(arg instanceof String) || StringUtils.isNotBlank((String) arg);
            assert !(arg instanceof ByteBuffer) || ((ByteBuffer) arg).remaining() > 0;
            assert !(arg instanceof Collection) || ((Collection) arg).size() > 0;
            assert null != arg;
        }
    }

}
