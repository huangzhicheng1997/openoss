package io.oss.kernel.support.processor;

import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhicheng
 * @Date 2021/6/23 9:13 下午
 * @Version 1.0
 */
public class CompositeProcessorOwner implements FindDependenciesComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String/*URI*/, NettyProcessor> processorMapping = new ConcurrentHashMap<>();

    @Override
    public void setComponentDependencies(Map<String, Component> componentMap) {
        componentMap.values().stream().filter(component -> {
            if (null == component) {
                return false;
            }
            return component instanceof NettyProcessor;
        }).forEach(component ->
                {
                    NettyProcessor nettyProcessor = (NettyProcessor) component;
                    List<String> mappingUris = nettyProcessor.getMappingUri();
                    //多个uri对应一个processor
                    mappingUris.forEach(uri -> {
                                processorMapping.put(uri, nettyProcessor);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("mapping uri:\"" + uri + "\"  processorName:" + nettyProcessor.getName() + " is registered");
                                }
                            }
                    );
                }
        );
    }

    public Map<String/*URI*/, NettyProcessor> processorMapping() {
        return processorMapping;
    }
}
