package io.oss.file.service;

import io.oss.util.util.KVPair;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhicheng
 * @Date 2021/6/29 8:55 下午
 * @Version 1.0
 */
public class HashIndex {
    /**
     * 索引的最⼤槽位 10M
     */
    private static final int MAXIMUM_CAPACITY = 10 * (1 << 20);

    private static final int MAXIMUM_MAPPING_LENGTH = 256;
    /**
     * hash索引名
     */
    public static final String INDEX_NAME = ".index";
    /**
     * ⼀个hash槽的⼤⼩
     */
    private static final Integer SLOT_SIZE = 8;
    /**
     * node⻓度
     */
    private static final Integer NODE_SIZE = 310;
    /**
     * 索引位置
     */
    private final String dirPath;
    /**
     * 哈希槽位
     */
    private final Integer hashSlotSize;
    /**
     * 索引⽂件
     */
    private RandomAccessFile indexFile;
    /**
     * 索引⽂件信息
     */
    private File file;

    /**
     * @param hashSlotSize hash表槽位
     * @param dirPath      存放索引的目录
     */
    public HashIndex(Integer hashSlotSize, String dirPath) {
        this.hashSlotSize = slotSizeFor(hashSlotSize);
        this.dirPath = dirPath;
        loadIndexFile(dirPath);
    }

    /**
     * 索引中追加文件
     */
    public synchronized void append(File file, Long fullFileLength) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        String path = file.getPath();
        Slot slot = findSlot(path);
        Node exist = slot.findNodeByFileName(file.getName());
        if (null == exist || exist.isDeleted == 1) {

            Node node = new Node(
                    file.length(),
                    fullFileLength,
                    file.lastModified(),
                    file.lastModified(),
                    (byte) 0,
                    this.file.length(),
                    file.getName() + ":" + file.getName(),
                    0,
                    (byte) (file.isDirectory() ? 1 : 0));

            slot.appendNode(node);
        } else {
            //更新文件大小，最新修改时间
            Node node = new Node(file.length(), exist.fullFileLength, file.lastModified(),
                    exist.createTime, exist.isDeleted, exist.offset,
                    exist.fileNameMapping, exist.nextNodeOffset, exist.isDir);

            node.writeToIndex();
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件绝对路径
     */
    public synchronized void delete(String filePath) {
        Node node = findNodeByFilePath(filePath);
        if (null == node || 1 == node.isDeleted) {
            return;
        }
        Slot slot = findSlot(filePath);
        new Node(node.fileLength, node.fullFileLength, node.lastModifyTime, node.createTime,
                (byte) 1, node.offset, node.fileNameMapping, node.nextNodeOffset, node.isDir)
                .writeToIndex();
    }

    /**
     * 重命名文件
     *
     * @param filePath  文件全路径
     * @param afterName 修改后的文件名
     * @return -1 不存在原文件  0 已存在其他同名文件 1成功
     */
    public synchronized int rename(String filePath, String afterName) {
        Node node = findNodeByFilePath(filePath);
        if (null == node || node.isDeleted == 1) {
            return -1;
        }
        if (null != findNodeByFilePath(afterName)) {
            return 0;
        }
        String originFileName = node.mappingFile().getV();
        node.fileNameMapping = afterName + ":" + originFileName;
        node.writeToIndex();
        return 1;
    }

    private Node findNodeByFilePath(String filePath) {
        Slot slot = findSlot(filePath);
        File file = new File(filePath);
        return slot.findNodeByFileName(file.getName());
    }

    private synchronized void loadIndexFile(String dirPath) {
        try {
            String indexFileName = dirPath + File.separator + INDEX_NAME;
            this.file = new File(indexFileName);
            if (!file.exists()) {
                file.createNewFile();
                this.indexFile = new RandomAccessFile(file, "rw");
                ByteBuffer direct = ByteBuffer.allocateDirect(this.hashSlotSize);
                indexFile.getChannel().write(direct, 0);
                direct.clear();
                return;
            }
            this.indexFile = new RandomAccessFile(file, "rw");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查找索引槽
     *
     * @param filePath 文件全路径
     * @return 槽位
     */
    private Slot findSlot(String filePath) {
        assert filePath != null && filePath.length() > 0;
        return new Slot(filePath);
    }


    private ByteBuffer read(Integer allocateBytes, Long position) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(allocateBytes);
            indexFile.getChannel().read(allocate, position);
            if (allocate.position() > 0) {
                allocate.flip();
            }
            return allocate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(ByteBuffer bufferIn, Long position) {
        try {
            indexFile.getChannel().write(bufferIn, position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Node readNode(long offset) {
        ByteBuffer buffer = read(NODE_SIZE, offset);
        long fileLength = buffer.getLong();
        long fullFileLength = buffer.getLong();
        long lastModifyTime = buffer.getLong();
        long createTime = buffer.getLong();
        byte isDeleted = buffer.get();
        byte isDir = buffer.get();
        long nextNodeOffset = buffer.getLong();
        long currentOffset = buffer.getLong();
        assert offset == currentOffset;
        int validFileNameMappingLength = buffer.getInt();
        byte[] fileNameMappingBytes = new byte[MAXIMUM_MAPPING_LENGTH];
        buffer.get(fileNameMappingBytes);
        byte[] validFileNameMappingBytes = new byte[validFileNameMappingLength];
        System.arraycopy(fileNameMappingBytes, 0, validFileNameMappingBytes, 0,
                validFileNameMappingLength);
        String validFileNameMapping = new String(validFileNameMappingBytes,
                StandardCharsets.UTF_8);
        return new Node(fileLength, fullFileLength, lastModifyTime,
                createTime, isDeleted, currentOffset,
                validFileNameMapping,
                nextNodeOffset, isDir);
    }

    private static int slotSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private int hash(String path) {
        int hash = hash0(path);
        //向下寻找最近的槽位
        return (hash / SLOT_SIZE) * SLOT_SIZE - 1;
    }

    private int hash0(String path) {
        return Math.abs(path.hashCode()) & (hashSlotSize - 1);
    }

    class Slot {

        private final Integer initialPosition;
        private Node head;
        private Node tail;

        Slot(String pathInChain) {
            assert pathInChain != null;
            //计算出hash槽初始点
            this.initialPosition = hash(pathInChain);
            load();
        }

        public Integer getInitialPosition() {
            return this.initialPosition;
        }

        boolean isEmpty() {
            return null == head;
        }

        void writeHead(Node node) {
            this.head = node;
            this.tail = node;
            ByteBuffer allocate = ByteBuffer.allocate(Long.BYTES);
            allocate.putLong(node.offset);
            allocate.flip();
            write(allocate, Long.valueOf(initialPosition));
        }

        /**
         * 加载整个链表
         */
        void load() {
            ByteBuffer buffer = read(SLOT_SIZE, Long.valueOf(initialPosition));
            long slotOffset = buffer.getLong();
            buffer = null;
            if (0 == slotOffset) {
                return;
            }
            this.head = readNode(slotOffset);
            loadNodeChain();
        }

        /**
         * 根据⽬标查找的⽂件的全路径查查询节点
         *
         * @param fileName
         * @return
         */
        Node findNodeByFileName(String fileName) {
            assert fileName != null && fileName.length() != 0;
            if (null == head) {
                return null;
            }
            Node temp = head;
            Node target = null;
            KVPair<String, String> mapping = temp.mappingFile();
            if (mapping.getKey().equals(fileName)) {
                target = head;
                return target;
            }
            while (temp.next != null) {
                temp = temp.next;
                KVPair<String, String> stringStringKVPair = temp.mappingFile();
                if (stringStringKVPair.getKey().equals(fileName)) {
                    target = temp;
                    break;
                }
            }
            return target;
        }

        /**
         * 末尾添加一个新Node节点
         *
         * @param newNode
         */
        private void appendNode(Node newNode) {
            //写入文件
            newNode.writeToIndex();
            //连接链表
            if (!isEmpty()) {
                this.tail.next = newNode;
                this.tail.nextNodeOffset = newNode.offset;
                this.tail.writeToIndex();
                this.tail = newNode;
            } else {
                writeHead(newNode);
            }


        }


        /**
         * 加载当前节点的所有下级节点
         *
         * @return Node 尾部节点
         */
        private void loadNodeChain() {
            Node nextNode = head;
            Node lastNode = head;
            //存在下⼀个节点
            while (nextNode != null) {
                lastNode = nextNode;
                nextNode = nextNode.loadNextNode(nextNode);
            }
            this.tail = lastNode;
        }
    }

    class Node {
        /**
         * ⽂件⻓度
         */
        private long fileLength;
        /**
         * 完整文件的长度
         */
        private long fullFileLength;
        /**
         * 更新时间
         */
        private long lastModifyTime;
        /**
         * 创建时间
         */
        private long createTime;
        /**
         * 是否被删除 1删除 0未删除
         */
        private byte isDeleted;
        /**
         * 是否为⽬录 1⽬录，2⽂件
         */
        private byte isDir;

        /**
         * 下⼀个节点的所在⽂件的偏移
         */
        private long nextNodeOffset;
        /**
         * 当前节点的offset
         */
        private long offset;
        /**
         * ⽂件映射信息的有效⻓度
         */
        private int validFileNameMappingLength;
        /**
         * ⽂件名映射 格式为 'latestFileName:mappedFileName'
         * 默认固定占256个字节
         */
        private String fileNameMapping;
        /**
         * 下⼀个⽂件节点
         */
        private Node next;

        Node(long fileLength, long fullFileLength, long lastModifyTime,
             long createTime, byte isDeleted, long offset,
             String fileNameMapping, long nextNodeOffset, byte isDir) {
            this.fileLength = fileLength;
            this.fullFileLength = fullFileLength;
            this.lastModifyTime = lastModifyTime;
            this.createTime = createTime;
            this.isDeleted = isDeleted;
            this.offset = offset;
            this.isDir = isDir;
            this.fileNameMapping = fileNameMapping;
            this.validFileNameMappingLength = this.fileNameMapping.getBytes().length;
            this.nextNodeOffset = nextNodeOffset;
        }

        /**
         * 解析映射 key为⽬前的⽂件名，value为原⽂件名
         *
         * @return
         */
        KVPair<String, String> mappingFile() {
            assert fileNameMapping != null;
            String[] mapping = fileNameMapping.split(":");
            return new KVPair<>(mapping[0], mapping[1]);
        }

        /**
         * 加载node的下⼀个节点
         *
         * @param node 节点
         * @return Node
         */
        private Node loadNextNode(Node node) {
            //当前节点不存在下⼀个节点
            if (node.nextNodeOffset == 0) {
                return null;
            }
            node.next = readNode(node.nextNodeOffset);
            return node.next;
        }

        /**
         * Node对象序列化
         *
         * @return ByteBuffer
         */
        ByteBuffer toBuffer() {
            ByteBuffer allocate = ByteBuffer.allocate(NODE_SIZE);
            allocate.putLong(this.fileLength)
                    .putLong(this.fullFileLength)
                    .putLong(this.lastModifyTime)
                    .putLong(this.createTime)
                    .put(this.isDeleted)
                    .put(this.isDir)
                    .putLong(this.nextNodeOffset)
                    .putLong(this.offset)
                    .putInt(this.validFileNameMappingLength);

            byte[] fileNameMappingBytes = new byte[MAXIMUM_MAPPING_LENGTH];
            System.arraycopy(this.fileNameMapping.getBytes(), 0, fileNameMappingBytes, 0, this.fileNameMapping.length());

            allocate.put(fileNameMappingBytes);
            allocate.flip();
            return allocate;
        }


        void writeToIndex() {
            try {
                indexFile.getChannel().write(toBuffer(), offset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HashIndex hashIndex = new HashIndex(MAXIMUM_CAPACITY, "/Users/huangzhicheng/Desktop/hzc");

       /* for (int i = 0; i < 10000; i++) {
            File file = new File("/Users/huangzhicheng/Desktop/hzc/xx" + i + ".txt");
            file.createNewFile();
            hashIndex.append(file, file.length());
        }*/
        /*   hashIndex.rename("C:\\Users\\lszhichengh\\Desktop\\class\\xx8999.txt","hzc.txt");*/
       /* for (int i = 0; i < 10000; i++) {

            Thread.sleep(100);
            Random random = new Random();
            int i1 = random.nextInt(9999);
            long start = System.currentTimeMillis();
            Node nodeByFilePath = hashIndex.findNodeByFilePath("/Users/huangzhicheng/Desktop/hzc/xx" + 9999 + ".txt");
            long end = System.currentTimeMillis();
            System.out.println(end - start);

        }*/
        /*File file = new File("/Users/huangzhicheng/Desktop/hzc");
        file.listFiles(pathname -> {
            if (pathname.getPath().equals("/Users/huangzhicheng/Desktop/hzc/xx3672.txt")) {
                System.out.println("xxx");
            }
            try {
                hashIndex.append(pathname, pathname.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });*/


    }
}