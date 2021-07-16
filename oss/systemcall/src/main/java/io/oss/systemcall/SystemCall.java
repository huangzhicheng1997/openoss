package io.oss.systemcall;

import com.sun.jna.*;

/**
 * 系统调用
 *
 * @Author zhicheng
 * @Date 2021/5/27 10:34 上午
 * @Version 1.0
 */
public interface SystemCall extends Library {

    int MADV_NORMAL = 0;       /* [MC1] no further special treatment */
    int MADV_RANDOM = 1;       /* [MC1] expect random page refs */
    int MADV_SEQUENTIAL = 2;      /* [MC1] expect sequential page refs */
    int MADV_WILLNEED = 3;      /* [MC1] will need these pages */
    int MADV_DONTNEED = 4;     /* [MC1] dont need these pages */


    SystemCall INSTANCE = (SystemCall) Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", SystemCall.class);

    /**
     * 系统调用，页面读取时预读优化策略
     *
     * @param addr   地址指针
     * @param length 长度
     * @param advice 建议
     * @return
     */
    int madvise(Pointer addr, NativeLong length, int advice);

    /**
     * 获取页面大小（一般4096个字节）
     *
     * @return 页面大小
     */
    int getpagesize();


    int F_OK = 0; /* test for existence of file */
    int X_OK = 1;  /* test for execute or search permission */
    int W_OK = (1 << 1); /* test for write permission */
    int R_OK = (1 << 2);/* test for read permission */


    /**
     * 判断文件是否存在
     *
     * @param PATH
     * @param F_OK
     * @return
     */
    int access(String PATH, int F_OK);
}
