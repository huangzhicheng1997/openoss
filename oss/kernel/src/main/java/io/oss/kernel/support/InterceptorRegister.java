package io.oss.kernel.support;

import io.oss.kernel.impl.ResponseSeqInjectInterceptor;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.support.processor.ProcessorInterceptor;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author zhicheng
 * @date 2021-05-07 17:24
 */
public class InterceptorRegister implements FindDependenciesComponent {

    private LinkedList<ProcessorInterceptor> processorInterceptors = new LinkedList<>();

    public void addLast(ProcessorInterceptor processorInterceptor) {
        processorInterceptors.addLast(processorInterceptor);
    }

    public void addFirst(ProcessorInterceptor processorInterceptor) {
        processorInterceptors.addFirst(processorInterceptor);
    }

    public void addBefore(ProcessorInterceptor target, ProcessorInterceptor insert) {
        int index = processorInterceptors.indexOf(target);
        processorInterceptors.add(index, insert);
    }

    public void addAfter(ProcessorInterceptor target, ProcessorInterceptor insert) {
        int index = processorInterceptors.indexOf(target);
        processorInterceptors.add(index + 1, insert);
    }

    public LinkedList<ProcessorInterceptor> getProcessorInterceptors() {
        return processorInterceptors;
    }

    @Override
    public void setComponentDependencies(Map<String, Component> componentMap) {
        componentMap.values().stream()
                .filter(component -> component instanceof ProcessorInterceptor)
                .map(component -> (ProcessorInterceptor) component)
                .sorted(Comparator.comparing(ProcessorInterceptor::getOrder))
                .forEach(this::addLast);


    }
}
