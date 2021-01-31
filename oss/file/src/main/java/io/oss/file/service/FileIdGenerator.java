package io.oss.file.service;

/**
 * @author zhicheng
 * @date 2021-01-18 15:42
 */
public class FileIdGenerator {
    //context
    public  int hash(String fileName){
        return fileName.hashCode();
    }
}
