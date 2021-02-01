package io.oss.file.upload;

import io.oss.file.service.FileIdGenerator;
import io.oss.util.util.FileUtil;
import org.omg.CORBA.portable.UnknownException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * @author zhicheng
 * @date 2021-01-18 14:54
 */
public class TempFile {

    private final File file;
    /**
     * 正确的写入偏移量
     */
    private long writeOffset = 0;

    private final FileIdGenerator fileIdGenerator = new FileIdGenerator();

    //完整文件大小
    private final long completeFileByteSize;

    private boolean isOver;

    private final RandomAccessFile randomFile;

    private String originFileName;

    public TempFile(String fileName, String storeDir, Long completeFileByteSize) throws FileNotFoundException {
        this.originFileName = fileName;
        this.file = new File(storeDir + File.separator + fileName + "#temp" + fileIdGenerator.hash(fileName));
        if (file.isDirectory()) {
            throw new RuntimeException("fileName is directory");
        }
        if (file.length() == completeFileByteSize) {
            isOver = true;
        }
        File directory = new File(file.getParent());
        if (!directory.exists()) {
            directory.mkdir();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                //如果文件存在初始化服务器上传的临时保存的文件的最大偏移量
                writeOffset = file.length();
            }
        } catch (IOException e) {
            throw new UnknownException(e);
        }
        this.completeFileByteSize = completeFileByteSize;
        this.randomFile = new RandomAccessFile(file, "rw");

    }


    /**
     * 顺序写
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public long write(ByteBuffer buffer) throws IOException {
        FileChannel channel = randomFile.getChannel();
        int written = channel.write(buffer, writeOffset);
        //update next write start offset
        writeOffset = writeOffset + written;
        if (writeOffset >= completeFileByteSize) {
            isOver = true;
            return -1;
        }
        channel.force(false);
        return written;
    }


    public void finishFile() throws IOException {
        if (isOver) {
            randomFile.close();
            String tempFileName = this.file.getName();
            String originPathNameWithSuffix = this.file.getParent() + File.separator + FileUtil.getOriginNameForTempFile(tempFileName);
            File originFile = new File(originPathNameWithSuffix);

            //以存在文件则重命名为（1），（2）......
            if (originFile.exists()) {
                File parentDir = new File(file.getParent());

                String fileOrgNameWithoutSuffix = FileUtil.getFileNameWithoutSuffix(originFile.getName());
                //查询同名,不包括文件序号而且后缀名相同 去除临时文件

                String[] fileList = parentDir.list((dir, fileName) ->
                        fileName.startsWith(fileOrgNameWithoutSuffix)
                                && FileUtil.getFileSuffix(fileName).equals(FileUtil.getFileSuffix(originFile.getName()))
                                && !fileName.equals(tempFileName)
                );

                //如果查不到直接重命名
                if (null == fileList) {
                    Files.move(this.file.toPath(), FileSystems.getDefault().getPath(originPathNameWithSuffix));
                    return;
                }

                //文件名
                String filePathName = FileUtil.getFileNameWithoutSuffix(originPathNameWithSuffix);
                //文件后缀名
                String fileSuffixName = FileUtil.getFileSuffix(originPathNameWithSuffix);
                //拼接文件序号
                originPathNameWithSuffix = filePathName + "(" + findMaxNumberOfFile(fileList, fileOrgNameWithoutSuffix) + ")." + fileSuffixName;
            }
            Files.move(this.file.toPath(), new File(originPathNameWithSuffix).toPath());
        } else {
            throw new RuntimeException("file");
        }
    }

    /**
     * 查询同名文件中的最大编号
     *
     * @param fileList
     * @param fileOrgNameWithoutSuffix
     * @return
     */
    private Integer findMaxNumberOfFile(String[] fileList, String fileOrgNameWithoutSuffix) {
        if (fileList.length == 1) {
            return 1;
        }
        int maxNumber = 1;
        for (String fileName : fileList) {
            //同名文件
            String originFileName = fileOrgNameWithoutSuffix + FileUtil.POINT + FileUtil.getFileSuffix(fileName);
            if (fileName.equals(originFileName)) {
                continue;
            }

            //截取文件最后的字符"(1)"中的数字
            String number = FileUtil.getFileNumber(fileName);
            if (maxNumber < Integer.parseInt(number)) {
                maxNumber = Integer.parseInt(number);
            }
        }
        //已存在的文件编号+1
        return maxNumber + 1;
    }

    public void closeChannel() {
        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void resetWriteOffset() {
        writeOffset = file.length();
    }

    public boolean isOver() {
        return isOver;
    }

    public long getTempFileOffset() {
        return this.writeOffset;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }
}
