package io.oss.kernel.spi;

import io.oss.kernel.exception.KernelException;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.util.ClassPathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * 组件插件加载器
 *
 * @Author zhicheng
 * @Date 2021/4/10 5:17 下午
 * @Version 1.0
 */
public class SpiLoader {

    private static final String LOAD_SPI_LOCATION = "META-INF/loader.spi";

    private static final String BASE_PACKAGE = "base.package";

    private static final Logger logger = LoggerFactory.getLogger(SpiLoader.class);

    @Deprecated
    public static List<Component> loadSpi(ClassLoader classLoader) {

        Set<String> basePackages = new HashSet<>();
        try {
            Enumeration<URL> urls = (classLoader != null ?
                    classLoader.getResources(LOAD_SPI_LOCATION) :
                    ClassLoader.getSystemResources(LOAD_SPI_LOCATION));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());
                properties.forEach(((k, v) -> {
                            // 添加包扫描路径
                            if (BASE_PACKAGE.equals(k) && v != null) {
                                String[] packages = ((String) v).split(",");
                                basePackages.addAll(Arrays.asList(packages));
                                if (logger.isDebugEnabled()) {
                                    logger.debug("load basePackages:" + v);
                                }
                            }
                        })
                );
            }
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Unable to load spi from location [" +
                    LOAD_SPI_LOCATION + "]", ioException);
        }


        return loadSpi(basePackages);
    }

    public static List<Component> loadSpiFromSystemProperties(ClassLoader classLoader) {
        String packages = System.getProperty("base.packages");
        if (null == packages) {
            throw new KernelException("no base package set!");
        }
        String[] packageArray = packages.split(",");

        return loadSpi(new HashSet<>(Arrays.asList(packageArray)));
    }


    private static List<Component> loadSpi(Set<String> basePackages) {
        //查找class文件
        List<Class> classes = new ArrayList<>();
        basePackages.forEach(path -> {
            ClassPathScanner classPathScanner = new ClassPathScanner(path);
            classPathScanner.doScan();
            classes.addAll(classPathScanner.getComponentClasses());
        });


        //初始化组件
        List<Component> components = new ArrayList<>();
        classes.forEach(clazz -> {
            if (Component.class.isAssignableFrom(clazz)
                    && !clazz.isInterface()
                    && !Modifier.isAbstract(clazz.getModifiers())
                    && !clazz.isAnonymousClass()) {
                try {
                    Component o = (Component) clazz.newInstance();
                    if (o.getName() != null) {
                        components.add(o);
                        if (logger.isDebugEnabled()) {
                            logger.debug("load component:" + o);
                        }
                    }
                } catch (Exception e) {
                    logger.error("component create instance error!", e);
                }
            }
        });
        return components;
    }

}
