package io.oss.file.service;

import com.google.gson.Gson;
import io.oss.util.exception.FileNotFindException;
import io.oss.util.util.FileUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author zhicheng
 * @date 2021-05-18 17:10
 */
public class UploadingFile {


    private RandomAccessFile accessFile;

    private volatile boolean isOpen;

    private String directoryName;

    private String fileName;

    private FileDescriptor fileDescriptor;

    private boolean isClosed;
    /**
     * 加载临时文件
     *
     * @param absolutePath 文件绝对路径
     */
    public synchronized void open(String absolutePath) throws IOException {
        if (isOpen) {
            return;
        }
        File file = new File(absolutePath);
        if (file.isDirectory()){
            throw new IllegalFileOptionException("not support directory");
        }
        loadBaseInfo(absolutePath);
        if (!file.exists()) {
            new File(directoryName).mkdirs();
            //创建文件
            file = new File(directoryName + File.separator + fileName);
            file.createNewFile();

        }

        this.accessFile = new RandomAccessFile(file, "rw");
        isOpen = true;
        fileDescriptor = accessFile.getFD();
    }

    /**
     * @return 是否打开文件上传
     */
    public synchronized boolean isOpen() {
        return isOpen;
    }

    private synchronized void loadBaseInfo(String filePath) {
        this.directoryName = filePath.substring(0, filePath.lastIndexOf(File.separator));
        this.fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }


    public synchronized void pushPartOfFileSync(ByteBuffer byteBuffer, long position) throws IOException {
        if (!isOpen) {
            throw new UploadingFileNotOpenException();
        }

        accessFile.getChannel().write(byteBuffer, position);
        //强制刷盘
        fileDescriptor.sync();
    }


    public synchronized void pushPartOfFileASync(ByteBuffer byteBuffer, long position) throws IOException {
        if (!isOpen) {
            throw new UploadingFileNotOpenException();
        }
        accessFile.getChannel().write(byteBuffer, position);
    }

    /**
     * 接受到文件上传终止报文，进行finish
     *
     * @throws IOException
     */
    public synchronized void finishUpload() throws IOException {
        close();
    }


    /**
     * 文件描述符资源是释放
     *
     * @return true:有效;false:无效
     */
    public synchronized boolean isClosed() {
        return isClosed;
    }


    public synchronized void close() throws IOException {
        accessFile.close();
        this.isClosed = true;
        isOpen = false;
    }

    /**
     * 用于客户端下载文件校验
     *
     * @return
     */
    public synchronized String checkInfo() throws IOException {
        /*
         * 随机获取指定偏移下的字节，以kv的json进行展示，用于客户端文件校验
         */
        List<Long> randomPosition = new ArrayList<>();
        for (int i = 0; i < 10; ) {
            long randomInt = (long) (Math.random() * 100);
            if (randomInt < accessFile.getChannel().size()) {
                randomPosition.add(randomInt);
                i++;
            }
        }

        Map<Long, Byte> map = new HashMap<>();
        ByteBuffer allocate = ByteBuffer.allocate(1);
        randomPosition.forEach(position -> {
            try {
                accessFile.getChannel().read(allocate, position);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte b = allocate.get();
            map.put(position, b);
            allocate.flip();
        });
        Gson gson = new Gson();
        return gson.toJson(map);
    }


    public static FileDescriptor getFD(int fd) {
        try {
            Method standardStream = FileDescriptor.class.getDeclaredMethod("standardStream", int.class);
            standardStream.setAccessible(true);
            return (FileDescriptor) standardStream.invoke(FileDescriptor.class, fd);
        } catch (Exception e) {
            //do nothing
        }
        return null;
    }

    public synchronized long length() throws IOException {
        return accessFile.length();
    }

}
