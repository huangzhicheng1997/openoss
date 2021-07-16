package io.oss.kernel.util;

import io.oss.kernel.exception.ComponentNotFindException;
import io.oss.kernel.exception.ModelNotFindException;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author zhicheng
 * @Date 2021/4/28 10:29 下午
 * @Version 1.0
 */
public class ClassPathScanner {

    private List<Class<?>> componentClasses = new ArrayList<>();

    private ClassLoader classLoader = ClassPathScanner.class.getClassLoader();

    private String basePackage;

    public ClassPathScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 获取包下所有实现了superStrategy的类并加入list
     */
    public void doScan() {
        URL url = classLoader.getResource(basePackage.replace(".", "/"));
        if (null == url) {
            throw new ModelNotFindException(basePackage + " not find!");
        }
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            // 本地代码
            scanAppClass(basePackage);
        } else if ("jar".equals(protocol)) {
            // libraries 内jar包
            findLibClass(basePackage);
        }
    }

    /**
     * 查找本工程的class
     *
     * @param basePackage 包路径
     */
    private void scanAppClass(final String basePackage) {
        URI url;
        try {
            url = classLoader.getResource(basePackage.replace(".", "/")).toURI();
        } catch (NullPointerException ex) {
            throw new ComponentNotFindException("not find resource please check basePackage:" + basePackage, ex);
        } catch (URISyntaxException ex) {
            throw new ComponentNotFindException(ex);
        }

        File file = new File(url);

        file.listFiles(resource -> {
            //目录继续递归
            if (resource.isDirectory()) {
                scanAppClass(basePackage + "." + resource.getName());
            }

            //class文件
            if (!resource.getName().endsWith(".class")) {
                return false;
            }

            Class<?> clazz;
            String componentStr = basePackage + "." + resource.getName().replace(".class", "");
            try {
                clazz = classLoader.loadClass(componentStr);
            } catch (ClassNotFoundException e) {
                throw new ComponentNotFindException("spi component not find, className:" + componentStr, e);
            }
            componentClasses.add(clazz);
            return true;
        });

    }

    public List<Class<?>> getComponentClasses() {
        return componentClasses;
    }

    /**
     * jar包查找
     *
     * @param basePackage 包路径
     */
    private void findLibClass(final String basePackage) {
        String pathName = basePackage.replace(".", "/");
        JarFile jarFile;
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + "/")) {
                if (jarEntry.isDirectory()) {
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    assert prefix != null;
                    findLibClass(prefix);
                }
                if (jarEntry.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        throw new ComponentNotFindException(e);
                    }

                    componentClasses.add(clazz);
                }
            }

        }


    }
}
