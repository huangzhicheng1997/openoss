package io.oss.kernel.support.processor;

import io.oss.kernel.spi.plugins.Component;
import io.oss.protocol.Command;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhicheng
 * @date 2021-04-29 17:17
 */
public interface NettyProcessor extends Component {

    List<String> getMappingUri();

    /**
     * Command返回null，则表示不回复，即实现ONE WAY语义。不会返回ack
     *
     * @param request
     * @param context
     * @return
     */
    Command handle(Command request, HandlerChainContext context);

    default ThreadPoolExecutor handlerExecutor() {
        return null;
    }
}
