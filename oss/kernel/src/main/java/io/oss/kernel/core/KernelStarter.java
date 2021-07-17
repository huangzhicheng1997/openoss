package io.oss.kernel.core;

import io.netty.util.internal.StringUtil;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.environment.KernelEnvironment;
import io.oss.kernel.environment.SystemEnvironment;
import io.oss.protocol.CodecHelp;
import io.oss.kernel.spi.SpiLoader;
import io.oss.kernel.spi.listener.ApplicationListener;
import io.oss.kernel.spi.plugins.*;
import io.oss.kernel.support.ApplicationEventMultiCaster;
import io.oss.kernel.support.ApplicationLifeCycleManager;
import io.oss.kernel.support.DefaultApplicationLifeCycleManager;
import io.oss.protocol.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author zhicheng
 * @Date 2021/4/10 5:02 下午
 * @Version 1.0
 */
public abstract class KernelStarter {

    /**
     * 内核运行环境参数
     */
    private final IsolatedEnvironment isolatedEnvironment = new IsolatedEnvironment();

    private List<Component> components;

    private Map<String, Component> componentMap;

    private final List<EnvironmentLoader> environmentLoaders = new CopyOnWriteArrayList<>();

    private final List<ApplicationListener> applicationListeners = new CopyOnWriteArrayList<>();

    private final List<NettyOptionConfigAware> nettyOptionConfigAwareList = new CopyOnWriteArrayList<>();

    private ApplicationEventMultiCaster eventMultiCaster;

    private ApplicationLifeCycleManager applicationLifeCycleManager;

    private NettyServerBooster nettyServerBooster;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public KernelStarter(String packages) {
        System.setProperty("base.packages", packages);
    }

    public void run() {
        //查找插件
        spiRefresh();
        //创建生命周期管理器
        createApplicationLifeCycleManager();
        //依赖下发
        dependenciesNotify();
        //初始运行环境
        initEnvironment();
        //准备运行
        prepareRun();

        fireUp();
    }


    private void fireUp() {

        int port = 8999;

        String serverPortProperty = isolatedEnvironment
                .getPrivateProperty(KernelEnvironment.KernelEnvironmentName, KernelEnvironment.SERVER_PORT);
        if (!StringUtil.isNullOrEmpty(serverPortProperty)) {
            port = Integer.parseInt(serverPortProperty);
        }

        this.nettyServerBooster
                = new NettyServerBooster(
                codecHelp(), isolatedEnvironment,
                new TreeMap<>(componentMap), port);

        this.nettyServerBooster.fireAndStart();
    }


    /**
     * 创建协议工厂由子类决定
     */
    protected abstract CommandFactory createProtocolCommandFactory0();

    protected abstract CodecHelp codecHelp();

    /**
     * 内核生命周期hook的管理
     */
    private void createApplicationLifeCycleManager() {
        this.eventMultiCaster = new ApplicationEventMultiCaster();
        this.eventMultiCaster.setApplicationListeners(applicationListeners);
        this.applicationLifeCycleManager = new DefaultApplicationLifeCycleManager(eventMultiCaster, components);
        this.componentMap.put("io.oss.kernel.support.ApplicationEventMultiCaster", eventMultiCaster);
        this.componentMap.put("io.oss.kernel.support.DefaultApplicationLifeCycleManager", (Component) applicationLifeCycleManager);
        this.eventMultiCaster.init();
    }

    /**
     * 初始化运行环境
     */
    private void initEnvironment() {
        environmentLoaders.forEach(environmentLoader -> {
            if (environmentLoader != null) {
                isolatedEnvironment.registerNameSpace(environmentLoader.loadEnvironment());
            }
        });
        isolatedEnvironment.registerNameSpace(new SystemEnvironment());
        applicationLifeCycleManager.afterEnvironmentPrepared(isolatedEnvironment);

    }

    /**
     * 初始化spi相关实例
     */
    private void spiRefresh() {
        //加载组件
        // 递归时会动态修改数组此处用写时复制进行处理，保证不会出现并发修改异常
        this.components = new CopyOnWriteArrayList<>(SpiLoader.loadSpiFromSystemProperties(getClassLoader()));
        //转换为字典
        this.componentMap = this.components.stream().collect(Collectors.toMap(Component::getName, component -> component));
        //处理ComponentFactory生成的组件
        spiRefresh0(components);
    }

    /**
     * 检索所有的插件
     *
     * @param components 组件
     */
    private void spiRefresh0(List<Component> components) {
        Map<String, Component> factoryComponents = new HashMap<>();
        components.forEach(component -> {
            if (component instanceof ComponentFactory) {
                Map<String, Component> waitToAdd = ((ComponentFactory) component).getComponents();
                if (waitToAdd.size() != 0) {
                    spiRefresh0(new ArrayList<>(waitToAdd.values()));
                    factoryComponents.putAll(waitToAdd);
                    if (logger.isDebugEnabled()) {
                        logger.debug("load  components from componentFactory:" + waitToAdd.toString());
                    }
                }
            }

            if (component instanceof EnvironmentLoader) {
                this.environmentLoaders.add((EnvironmentLoader) component);
            }

            if (component instanceof ApplicationListener) {
                this.applicationListeners.add((ApplicationListener) component);
            }

            if (component instanceof NettyOptionConfigAware) {
                this.nettyOptionConfigAwareList.add((NettyOptionConfigAware) component);
            }
        });

        //补全组件（原组件+ComponentFactory定义的组件）
        this.components.addAll(new ArrayList<>(factoryComponents.values()));
        this.componentMap.putAll(factoryComponents);
    }

    private void dependenciesNotify() {
        //下发插件集合，用于依赖装配
        components.forEach(component -> {
            if (component instanceof FindDependenciesComponent) {
                //deep copy
                Map<String, Component> componentMap = new HashMap<>(this.componentMap);
                ((FindDependenciesComponent) component).setComponentDependencies(componentMap);
            }
        });
    }


    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 启动过程中的前期准备,包括对组件进行管理
     */
    private void prepareRun() {
        //准备
        applicationLifeCycleManager.afterPreparePlugins();
    }
}
